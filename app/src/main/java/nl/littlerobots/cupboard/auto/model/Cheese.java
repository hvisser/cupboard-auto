package nl.littlerobots.cupboard.auto.model;

import com.google.auto.value.AutoValue;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class Cheese {
    public static Cheese create(Long _id, String name) {
        return new AutoValue_Cheese(_id, name);
    }

    @Nullable
    public abstract Long _id();

    public abstract String name();
}
