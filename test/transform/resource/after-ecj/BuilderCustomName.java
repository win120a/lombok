import java.util.List;

@lombok.experimental.SuperBuilder
class BuilderCustomName<T> {
    public static abstract @java.lang.SuppressWarnings("all") class SimpleTestBuilder<T, C extends BuilderCustomName<T>, B extends BuilderCustomName.SimpleTestBuilder<T, C, B>> {
        private @java.lang.SuppressWarnings("all") int field;

        public SimpleTestBuilder() {
            super();
        }

        /**
         * @return {@code this}.
         */
        public @java.lang.SuppressWarnings("all") B field(final int field) {
            this.field = field;
            return self();
        }

        protected abstract @java.lang.SuppressWarnings("all") B self();

        public abstract @java.lang.SuppressWarnings("all") C build();

        public @java.lang.Override
        @java.lang.SuppressWarnings("all") java.lang.String toString() {
            return (("BuilderCustomName.SimpleTestBuilder(field=" + this.field) + ")");
        }
    }

    private static final @java.lang.SuppressWarnings("all") class SimpleTestBuilderImpl<T> extends BuilderCustomName.SimpleTestBuilder<T, BuilderCustomName<T>, BuilderCustomName.SimpleTestBuilderImpl<T>> {
        private SimpleTestBuilderImpl() {
            super();
        }

        protected @java.lang.Override
        @java.lang.SuppressWarnings("all") BuilderCustomName.SimpleTestBuilderImpl<T> self() {
            return this;
        }

        public @java.lang.Override
        @java.lang.SuppressWarnings("all") BuilderCustomName<T> build() {
            return new BuilderCustomName<T>(this);
        }
    }

    private final int field;

    protected @java.lang.SuppressWarnings("all") BuilderCustomName(final BuilderCustomName.SimpleTestBuilder<T, ?, ?> b) {
        super();
        this.field = b.field;
    }

    public static @java.lang.SuppressWarnings("all") <T> BuilderCustomName.SimpleTestBuilder<T, ?, ?> builder() {
        return new BuilderCustomName.SimpleTestBuilderImpl<T>();
    }
}
