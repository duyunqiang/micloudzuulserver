# Zuul Server

Run this app as a normal Spring Boot app. If you run from this project 
it will be on port 8765 (per the `application.yml`). Also run the
[stores](https://github.com/spring-cloud-samples/customers-stores/tree/master/rest-microservices-store) 
and [customers](https://github.com/spring-cloud-samples/customers-stores/tree/master/rest-microservices-customers) 
samples from the [customer-stores](https://github.com/spring-cloud-samples/customers-stores) 
sample.  

You should then be able to view json content from 
`http://localhost:8765/stores` and `http://localhost:8765/customers` which are
configured in `application.yml` as proxy routes.


---
TO MAKE THINGS EASIER I ADDED A START UP SCRIPT THAT CAN BE USED FOR DEVELOPMENT.
You can run the ```./start.sh``` script and if you want to debug use ```./start.sh debug```.

IF YOU WANT TO DEBUG:
    In Intellij open the toolbar and select Run --> Edit Configurations 
    Then you will want to add a new configuration, select + --> Remote
    In the new Remote window you can name the configuration something useful
    Next under Settings, set Transport: Socket, Debugger Mode: Attach, Host: localhost, Port: 5007 (port the start.sh script uses)

---