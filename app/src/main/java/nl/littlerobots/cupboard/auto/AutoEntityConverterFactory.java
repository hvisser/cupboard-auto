package nl.littlerobots.cupboard.auto;

import java.lang.reflect.Modifier;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.EntityConverterFactory;

public class AutoEntityConverterFactory implements EntityConverterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> EntityConverter<T> create(Cupboard cupboard, final Class<T> type) {
        // assume any abstract class is an auto class
        if ((type.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
            try {
                return new AutoValueEntityConverter<>(cupboard, type, (Class<? extends T>) type.getClassLoader().loadClass(type.getPackage().getName() + ".AutoValue_" + type.getSimpleName()));
            } catch (ClassNotFoundException e) {
                // oops, not an auto class?
                return null;
            }
        }
        return null;
    }
}
