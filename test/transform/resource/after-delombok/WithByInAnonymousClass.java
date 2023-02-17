//version 8:
public class WithByInAnonymousClass {
    Object annonymous = new Object() {

        class Inner {
            private Inner(String string) {
            }

            private String string;

            @java.lang.SuppressWarnings("all")
            public Inner withStringBy(final java.util.function.Function<? super String, ? extends String> transformer) {
                return new Inner(transformer.apply(this.string));
            }
        }
    };
}
