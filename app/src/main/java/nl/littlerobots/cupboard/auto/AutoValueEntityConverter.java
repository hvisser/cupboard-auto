package nl.littlerobots.cupboard.auto;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/*
Quick and dirty converter for auto value. Lacks index info (for creating database indices), and more exotics like read only columns
 */
public class AutoValueEntityConverter<T> implements EntityConverter<T> {

    private final Constructor<T> mConstructor;
    private final Property[] mProperties;
    private final List<Column> mColumns;
    private final Class<T> mAbstractType;
    private Property mIdProperty;

    @SuppressWarnings("unchecked")
    AutoValueEntityConverter(Cupboard cupboard, Class<T> abstractType, Class<? extends T> autoValueType) {
        if ((abstractType.getModifiers() & Modifier.ABSTRACT) != Modifier.ABSTRACT) {
            throw new IllegalArgumentException("AutoValue classes should be abstract");
        }
        mAbstractType = abstractType;
        //TODO This won't get any inherited fields.
        Field[] fields = autoValueType.getDeclaredFields();
        mProperties = new Property[fields.length];
        mColumns = new ArrayList<>(mProperties.length);
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Property prop = new Property();
            Type type = fields[i].getGenericType();
            FieldConverter<?> converter = cupboard.getFieldConverter(type);
            if (converter == null) {
                throw new IllegalArgumentException("Do not know how to convert field " + fields[i].getName() + " in entity " + abstractType.getName() + " of type " + type);
            }
            prop.fieldConverter = (FieldConverter<Object>) converter;
            prop.field = fields[i];
            mProperties[i] = prop;
            if ("_id".equals(prop.field.getName())) {
                mIdProperty = prop;
            }
            mColumns.add(new Column(prop.field.getName(), converter.getColumnType()));
        }

        // TODO assume there's only one constructor that takes the parameters in order.
        mConstructor = (Constructor<T>) autoValueType.getDeclaredConstructors()[0];
        mConstructor.setAccessible(true);
    }

    @Override
    public T fromCursor(Cursor cursor) {
        Object[] values = new Object[mProperties.length];
        for (int i = 0; i < mProperties.length; i++) {
            values[i] = mProperties[i].fieldConverter.fromCursorValue(cursor, i);
        }
        try {
            return mConstructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toValues(T object, ContentValues values) {
        for (Property property : mProperties) {
            try {
                property.fieldConverter.toContentValue(property.field.get(object), property.field.getName(), values);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Column> getColumns() {
        return mColumns;
    }

    @Override
    public void setId(Long id, T instance) {
        // we can't mutate auto instances
    }

    @Override
    public Long getId(T instance) {
        if (mIdProperty != null) {
            try {
                return mIdProperty.field.getLong(instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String getTable() {
        return mAbstractType.getSimpleName();
    }

    private static class Property {
        Field field;
        FieldConverter<Object> fieldConverter;
    }
}
