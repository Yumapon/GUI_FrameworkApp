package container;

public interface ApplicationContainer {

	void beanDefinitionReload(String configFileName);

	Object generator(String instanceName);

	Object generator();

	//InstanceAndClassObjectforServlet getCAMS(String instanceName);

	Object getAction(String actionName);

}
