package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans = new HashSet<>();

    public DIContainer(final Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            beans.add(createNewBean(clazz));
        }
        for (Object bean : beans) {
            List<Field> fields = Arrays.stream(bean.getClass()
                            .getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList());

            for (Field field : fields) {
                for (Class<?> clazz : classes) {
                    if (field.getType().isAssignableFrom(clazz)) {
                        setField(bean, field, clazz);
                    }
                }
            }
        }
    }

    private void setField(final Object bean, final Field field, final Class<?> clazz) {
        try {
            field.setAccessible(true);
            field.set(bean, getBean(clazz));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createNewBean(final Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public static DIContainer createContainerForPackage(final String rootPackageName) {
        return new DIContainer(ClassPathScanner.getAllClassesInPackage(rootPackageName));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass().equals(aClass))
                .findAny()
                .orElseThrow();
    }
}
