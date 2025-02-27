package nextstep.study.di.stage4.annotations;

import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

public class ClassPathScanner {

    public static Set<Class<?>> getAllClassesInPackage(final String packageName) {
        HashSet<Class<?>> classes = new HashSet<>();
        Reflections reflections = new Reflections(packageName);
        classes.addAll(reflections.getTypesAnnotatedWith(Repository.class));
        classes.addAll(reflections.getTypesAnnotatedWith(Service.class));
        return classes;
    }
}
