package io.growing.dlsRpc;

public class HelloImpl implements Hello {

    @Override
    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
