# Zuul Server

Run this app as a normal Spring Boot app. If you run from this project
it will be on port 8765 (per the `application.yml`).

First run the [micloudconfigserver](https://github.com/MindsIgnited/micloudconfigserver)
Next run the [micloudeurekaserver](https://github.com/MindsIgnited/micloudeurekaserver)
Then run [micloudrestbase](https://github.com/MindsIgnited/micloudrestbase) which holds the rest service.
Lastly run this project.  It will pick up all possible services via Eureka and gets its config via ConfigService.

You should then be able to view json content from
`http://localhost:8765/micloudrestbase/devices/search/findByModel?model=MontaVista`
and
`http://localhost:8765/micloudrestbase/devices/1`
which forwards to the `micloudrestbase` service, with the remaining url. The service is found via eureka discovery.


---
TO MAKE THINGS EASIER I ADDED A START UP SCRIPT THAT CAN BE USED FOR DEVELOPMENT.
You can run the ```./start.sh``` script and if you want to debug use ```./start.sh debug```.

IF YOU WANT TO DEBUG:
    In Intellij open the toolbar and select Run --> Edit Configurations 
    Then you will want to add a new configuration, select + --> Remote
    In the new Remote window you can name the configuration something useful
    Next under Settings, set Transport: Socket, Debugger Mode: Attach, Host: localhost, Port: 5007 (port the start.sh script uses)

---