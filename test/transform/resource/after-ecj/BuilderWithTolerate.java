import lombok.Builder;
import lombok.experimental.Tolerate;

public @Builder class BuilderWithTolerate {
    public static class BuilderWithTolerateBuilder {
        private @java.lang.SuppressWarnings("all") int value;

        public @Tolerate BuilderWithTolerateBuilder value(String s) {
            return this.value(Integer.parseInt(s));
        }

        @java.lang.SuppressWarnings("all")
        BuilderWithTolerateBuilder() {
            super();
        }

        /**
         * @return {@code this}.
         */
        public @java.lang.SuppressWarnings("all") BuilderWithTolerate.BuilderWithTolerateBuilder value(final int value) {
            this.value = value;
            return this;
        }

        public @java.lang.SuppressWarnings("all") BuilderWithTolerate build() {
            return new BuilderWithTolerate(this.value);
        }

        public @java.lang.Override
        @java.lang.SuppressWarnings("all") java.lang.String toString() {
            return (("BuilderWithTolerate.BuilderWithTolerateBuilder(value=" + this.value) + ")");
        }
    }

    private final int value;

    public static void main(String[] args) {
        BuilderWithTolerate.builder().value("42").build();
    }

    @java.lang.SuppressWarnings("all")
    BuilderWithTolerate(final int value) {
        super();
        this.value = value;
    }

    public static @java.lang.SuppressWarnings("all") BuilderWithTolerate.BuilderWithTolerateBuilder builder() {
        return new BuilderWithTolerate.BuilderWithTolerateBuilder();
    }
}