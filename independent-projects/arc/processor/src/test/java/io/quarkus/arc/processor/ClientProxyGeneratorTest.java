package io.quarkus.arc.processor;

import static io.quarkus.arc.processor.Basics.index;

import io.quarkus.arc.processor.BeanProcessor.PrivateMembersCollector;
import io.quarkus.arc.processor.ResourceOutput.Resource;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import org.jboss.jandex.Index;
import org.junit.Test;

public class ClientProxyGeneratorTest {

    @Test
    public void testGenerator() throws IOException {

        Index index = index(Producer.class, List.class, Collection.class, Iterable.class, AbstractList.class, MyList.class);
        BeanDeployment deployment = new BeanDeployment(index, null, null);
        deployment.init();

        BeanGenerator beanGenerator = new BeanGenerator(new AnnotationLiteralProcessor(true, TruePredicate.INSTANCE),
                TruePredicate.INSTANCE, new PrivateMembersCollector());
        ClientProxyGenerator proxyGenerator = new ClientProxyGenerator(TruePredicate.INSTANCE);

        deployment.getBeans().stream().filter(bean -> bean.getScope().isNormal()).forEach(bean -> {
            for (Resource resource : beanGenerator.generate(bean, ReflectionRegistration.NOOP)) {
                proxyGenerator.generate(bean, resource.getFullyQualifiedName(), ReflectionRegistration.NOOP);
            }
        });
        // TODO test generated bytecode
    }

    @Dependent
    static class Producer {

        @ApplicationScoped
        @Produces
        List<String> list() {
            return null;
        }

    }

    @ApplicationScoped
    static class MyList extends AbstractList<String> {

        @Override
        public String get(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        void myMethod() throws IOException {
        }

    }

}
