import lombok.Builder;
import lombok.Value;

public final @Value
@Builder class BuilderDefaults {
    public static @java.lang.SuppressWarnings("all") class BuilderDefaultsBuilder {
        private @java.lang.SuppressWarnings("all") int x$value;
        private @java.lang.SuppressWarnings("all") boolean x$set;
        private @java.lang.SuppressWarnings("all") String name;
        private @java.lang.SuppressWarnings("all") long z$value;
        private @java.lang.SuppressWarnings("all") boolean z$set;

        @java.lang.SuppressWarnings("all")
        BuilderDefaultsBuilder() {
            super();
        }

        /**
         * @return {@code this}.
         */
        public @java.lang.SuppressWarnings("all") BuilderDefaults.BuilderDefaultsBuilder x(final int x) {
            this.x$value = x;
            x$set = true;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public @java.lang.SuppressWarnings("all") BuilderDefaults.BuilderDefaultsBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public @java.lang.SuppressWarnings("all") BuilderDefaults.BuilderDefaultsBuilder z(final long z) {
            this.z$value = z;
            z$set = true;
            return this;
        }

        public @java.lang.SuppressWarnings("all") BuilderDefaults build() {
            int x$value = this.x$value;
            if ((!this.x$set))
                x$value = BuilderDefaults.$default$x();
            long z$value = this.z$value;
            if ((!this.z$set))
                z$value = BuilderDefaults.$default$z();
            return new BuilderDefaults(x$value, this.name, z$value);
        }

        public @java.lang.Override
        @java.lang.SuppressWarnings("all") java.lang.String toString() {
            return (((((("BuilderDefaults.BuilderDefaultsBuilder(x$value=" + this.x$value) + ", name=") + this.name) + ", z$value=") + this.z$value) + ")");
        }
    }

    private final @Builder.Default int x;
    private final String name;
    private final @Builder.Default long z;

    private static @java.lang.SuppressWarnings("all") int $default$x() {
        return 10;
    }

    private static @java.lang.SuppressWarnings("all") long $default$z() {
        return System.currentTimeMillis();
    }

    @java.lang.SuppressWarnings("all")
    BuilderDefaults(final int x, final String name, final long z) {
        super();
        this.x = x;
        this.name = name;
        this.z = z;
    }

    public static @java.lang.SuppressWarnings("all") BuilderDefaults.BuilderDefaultsBuilder builder() {
        return new BuilderDefaults.BuilderDefaultsBuilder();
    }

    public @java.lang.SuppressWarnings("all") int getX() {
        return this.x;
    }

    public @java.lang.SuppressWarnings("all") String getName() {
        return this.name;
    }

    public @java.lang.SuppressWarnings("all") long getZ() {
        return this.z;
    }

    public @java.lang.Override
    @java.lang.SuppressWarnings("all") boolean equals(final java.lang.Object o) {
        if ((o == this))
            return true;
        if ((!(o instanceof BuilderDefaults)))
            return false;
        final BuilderDefaults other = (BuilderDefaults) o;
        if ((this.getX() != other.getX()))
            return false;
        if ((this.getZ() != other.getZ()))
            return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (((this$name == null) ? (other$name != null) : (!this$name.equals(other$name))))
            return false;
        return true;
    }

    public @java.lang.Override
    @java.lang.SuppressWarnings("all") int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = ((result * PRIME) + this.getX());
        final long $z = this.getZ();
        result = ((result * PRIME) + (int) ($z ^ ($z >>> 32)));
        final java.lang.Object $name = this.getName();
        result = ((result * PRIME) + (($name == null) ? 43 : $name.hashCode()));
        return result;
    }

    public @java.lang.Override
    @java.lang.SuppressWarnings("all") java.lang.String toString() {
        return (((((("BuilderDefaults(x=" + this.getX()) + ", name=") + this.getName()) + ", z=") + this.getZ()) + ")");
    }
}