//skip-idempotent
@com.fasterxml.jackson.databind.annotation.JsonDeserialize
public class JacksonizedSuperBuilderWithJsonDeserialize {
    int field1;

    @java.lang.SuppressWarnings("all")
    public static abstract class JacksonizedSuperBuilderWithJsonDeserializeBuilder<C extends JacksonizedSuperBuilderWithJsonDeserialize, B extends JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilder<C, B>> {
        @java.lang.SuppressWarnings("all")
        private int field1;

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public B field1(final int field1) {
            this.field1 = field1;
            return self();
        }

        @java.lang.SuppressWarnings("all")
        protected abstract B self();

        @java.lang.SuppressWarnings("all")
        public abstract C build();

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilder(field1=" + this.field1 + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    private static final class JacksonizedSuperBuilderWithJsonDeserializeBuilderImpl extends JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilder<JacksonizedSuperBuilderWithJsonDeserialize, JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        private JacksonizedSuperBuilderWithJsonDeserializeBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        protected JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public JacksonizedSuperBuilderWithJsonDeserialize build() {
            return new JacksonizedSuperBuilderWithJsonDeserialize(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    protected JacksonizedSuperBuilderWithJsonDeserialize(final JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilder<?, ?> b) {
        this.field1 = b.field1;
    }

    @java.lang.SuppressWarnings("all")
    public static JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilder<?, ?> builder() {
        return new JacksonizedSuperBuilderWithJsonDeserialize.JacksonizedSuperBuilderWithJsonDeserializeBuilderImpl();
    }
}