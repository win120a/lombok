class WithMethodMarkedDeprecated {
    @Deprecated
    int annotation;
    /**
     * @deprecated
     */
    int javadoc;

    WithMethodMarkedDeprecated(int annotation, int javadoc) {
    }

    /**
     * @return a clone of this object, except with this updated property (returns {@code this} if an identical value is passed).
     */
    @java.lang.Deprecated
    @java.lang.SuppressWarnings("all")
    public WithMethodMarkedDeprecated withAnnotation(final int annotation) {
        return this.annotation == annotation ? this : new WithMethodMarkedDeprecated(annotation, this.javadoc);
    }

    /**
     * @return a clone of this object, except with this updated property (returns {@code this} if an identical value is passed).
     * @deprecated
     */
    @java.lang.Deprecated
    @java.lang.SuppressWarnings("all")
    public WithMethodMarkedDeprecated withJavadoc(final int javadoc) {
        return this.javadoc == javadoc ? this : new WithMethodMarkedDeprecated(this.annotation, javadoc);
    }
}
