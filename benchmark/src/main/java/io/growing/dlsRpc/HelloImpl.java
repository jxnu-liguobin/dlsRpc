package io.growing.dlsRpc;

import com.typesafe.scalalogging.LazyLogging;
import com.typesafe.scalalogging.Logger;

public class HelloImpl implements Hello, LazyLogging {

    @Override
    public String sayHello(String name) {
        return name.concat("-hello!");
    }

    @Override
    public Logger logger() {
        return null;
    }
}
