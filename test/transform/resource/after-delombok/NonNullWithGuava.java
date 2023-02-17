//version 8:

import static com.google.common.base.Preconditions.*;

public class NonNullWithGuava {
    @lombok.NonNull
    private String test;

    public void testMethod(@lombok.NonNull String arg) {
        com.google.common.base.Preconditions.checkNotNull(arg, "arg is marked non-null but is null");
        System.out.println(arg);
    }

    public void testMethodWithCheck1(@lombok.NonNull String arg) {
        checkNotNull(arg);
    }

    public void testMethodWithCheckAssign(@lombok.NonNull String arg) {
        test = checkNotNull(arg);
    }

    public void testMethodWithCheck2(@lombok.NonNull String arg) {
        com.google.common.base.Preconditions.checkNotNull(arg);
    }

    public void testMethodWithFakeCheck1(@lombok.NonNull String arg) {
        com.google.common.base.Preconditions.checkNotNull(arg, "arg is marked non-null but is null");
        checkNotNull("");
    }

    public void testMethodWithFakeCheck2(@lombok.NonNull String arg) {
        com.google.common.base.Preconditions.checkNotNull(arg, "arg is marked non-null but is null");
        com.google.common.base.Preconditions.checkNotNull(test);
    }

    public void testMethodWithFakeCheckAssign(@lombok.NonNull String arg) {
        com.google.common.base.Preconditions.checkNotNull(arg, "arg is marked non-null but is null");
        test = checkNotNull(test);
    }

    @java.lang.SuppressWarnings("all")
    public void setTest(@lombok.NonNull final String test) {
        com.google.common.base.Preconditions.checkNotNull(test, "test is marked non-null but is null");
        this.test = test;
    }
}