// version 8:

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

class ThisParameter {

    void untagged(ThisParameter this, int i) {
        // no content
    }

    void sourceTagged(@SourceTagged("source")ThisParameter this) {
        // no content
    }

    void classTagged(@ClassTagged("class")ThisParameter this) {
        // no content
    }

    void runtimeTagged(@RuntimeTagged("runtime")ThisParameter this) {
        // no content
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.SOURCE)
    @interface SourceTagged {
        String value();
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.CLASS)
    @interface ClassTagged {
        String value();
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RuntimeTagged {
        String value();
    }
}
