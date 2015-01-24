# Zuul Server

---

Run this app as a normal Spring Boot app. If you run from this project
it will be on port 8765 (per the `application.yml` or start.sh).

TO MAKE THINGS EASIER I ADDED A START UP SCRIPT THAT CAN BE USED FOR DEVELOPMENT.
You can run the ```./start.sh``` script and if you want to debug use ```./start.sh debug```.

IF YOU WANT TO DEBUG:
    In Intellij open the toolbar and select Run --> Edit Configurations
    Then you will want to add a new configuration, select + --> Remote
    In the new Remote window you can name the configuration something useful
    Next under Settings, set Transport: Socket, Debugger Mode: Attach, Host: localhost, Port: 5007 (port the start.sh script uses)

---

First run the [micloudconfigserver](https://github.com/MindsIgnited/micloudconfigserver)

Next run the [micloudeurekaserver](https://github.com/MindsIgnited/micloudeurekaserver)

Then run [micloudrestbase](https://github.com/MindsIgnited/micloudrestbase) which holds the rest service.
NOTE: This is the database configuration for this service:

      url: jdbc:mysql://localhost:3306/restbase
      username: root
      password: pass79
      driver-class-name: com.mysql.jdbc.Driver

Then ensure that you have Keycloak intalled locally, running on 127.0.0.1. Please also upload the oauth2-test-realm.json under Add Realm in Keycloak.
*TIP:* give it about 2 minutes to allow all services to be fully up with eureka. needs three heartbeats @ 30 sec intervals. NOTE: still looking at how we can get it to register with Zuul when it comes up on eureka dynamically.

Lastly run this project.  It will pick up all possible services via Eureka and gets its config via ConfigService.

---

You should then be able to view json content from
`http://127.0.0.1:8765/micloudrestbase/devices/search/findByModel?model=MontaVista`
and
`http://127.0.0.1:8765/micloudrestbase/devices/1`
which forwards to the `micloudrestbase` service, with the remaining url. The service is found via eureka discovery.

You can also run the [micloudhystrixserver](https://github.com/MindsIgnited/micloudhystrixserver) as a service and hit it via this url:
`http://127.0.0.1:8765/micloudhystrix/` <-- We need the extra / so that it goes to the root mapping - figure out how to allow it without the extra /

You will just need to register a new user, allow access to the services and you should get your response.

---
A direct access grant can be obtained following this example :

```curl -v -XPOST "http://oauth-api-client:293490bd-45bf-460a-82e4-aa11ffcf3d80@127.0.0.1:8080/auth/realms/oauth2-test/tokens/grants/access" -d "username=npadilla&password=TheTh1ng"```

This will produce a json response that will contain the `access_token`, like this one

```{"access_token":"eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwNmEyZTkxNC01YjM2LTQ4OGQtOTcyMy1iMDAxMjA2M2Q2OTIiLCJleHAiOjE0MjE4NzY5MTYsIm5iZiI6MCwiaWF0IjoxNDIxODc2NjE2LCJpc3MiOiJvYXV0aDItdGVzdCIsImF1ZCI6Im9hdXRoLWFwaS1jbGllbnQiLCJzdWIiOiJkY2VmZjc2ZC0xNjAzLTQ3NGQtOTViNi0zYjc3YzlhNTEzZGMiLCJhenAiOiJvYXV0aC1hcGktY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjU0M2MxMWQ4LWRlYjQtNDE4NC1iYzA0LWMxZmZiZTRjOGQxZiIsImFsbG93ZWQtb3JpZ2lucyI6W10sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibWljbG91ZGh5c3RyaXgiOnsicm9sZXMiOlsidXNlciJdfSwibWljbG91ZHJlc3RiYXNlIjp7InJvbGVzIjpbIlJFQUQiLCJXUklURSJdfX19.Sc3ivDPrsDiEWfDuQIz44iZ0hLwO3Vxc9xfI_asZCgUCS-btrAmGBiDyrFbXbUgV9dQRv84OBElBXvVK97B3GfAFv6olOesENbK3prWEP-P43RV_qBbY3Pbs9YygtAB9PC5IgN8TqUy73pI3fcifdCDuUELylXnTClCa9-Vv4gw","expires_in":300,"refresh_token":"eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI0YWQyNTEwOC0yZmUwLTQ2ZjEtOTJkNS1iYzg0ZmJkMjc0ZjgiLCJleHAiOjE0MjE4Nzg0MTYsIm5iZiI6MCwiaWF0IjoxNDIxODc2NjE2LCJpc3MiOiJvYXV0aDItdGVzdCIsInN1YiI6ImRjZWZmNzZkLTE2MDMtNDc0ZC05NWI2LTNiNzdjOWE1MTNkYyIsInR5cCI6IlJFRlJFU0giLCJhenAiOiJvYXV0aC1hcGktY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjU0M2MxMWQ4LWRlYjQtNDE4NC1iYzA0LWMxZmZiZTRjOGQxZiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibWljbG91ZGh5c3RyaXgiOnsicm9sZXMiOlsidXNlciJdfSwibWljbG91ZHJlc3RiYXNlIjp7InJvbGVzIjpbIlJFQUQiLCJXUklURSJdfX19.XCF3vtPmD0PxlTkkQL93j0ArCoxWCo7XwWY8SRNqaYENSdMG0iUn8ulsdB0QaZwFqTOpDlfzzwlHyb7RW7YxOkLzInaUPihYKfq2dD0Nagzyz44tKC-rHZSvBnuLUoa_wpLHtG_sy8Vpu5Wq1PafxlEnuy-kOTaBuMsaPCc--a8","token_type":"bearer","id_token":"eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIzNmMwMmNlMy1lMWNlLTQzNDMtYTIyYi1iNGQ2ZTM1NTllYWYiLCJleHAiOjE0MjE4NzY5MTYsIm5iZiI6MCwiaWF0IjoxNDIxODc2NjE2LCJpc3MiOiJvYXV0aDItdGVzdCIsImF1ZCI6Im9hdXRoLWFwaS1jbGllbnQiLCJzdWIiOiJkY2VmZjc2ZC0xNjAzLTQ3NGQtOTViNi0zYjc3YzlhNTEzZGMiLCJhenAiOiJvYXV0aC1hcGktY2xpZW50In0.dn6I8GBVL-8aqweMErV9ChC5tylPVhn6Hx32XgRubZHhfEV68MvlKNx8pfqRmqFGy5wu612qvdStogO0POZ5Ds0VfGaRbJ_CI6wGsIy8r7HndOyKukBrFgBhU--UW1a4rBOBLk6e5l2kE82O8vOHh7ChgoBTrG8w-ikGzj92v-s","not-before-policy":1421870167,"session-state":"543c11d8-deb4-4184-bc04-c1ffbe4c8d1f"}```

you can then request the resource directly to the service.  We can also use Zuul but will need to be setup just as a Resource Server, using `@EnableOAuth2Resource`
but you will need to remove `@EnableOAuth2Sso`. Doing it this way will provide an example for both the GUI redirect and direct access grant.

```curl -v -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwNmEyZTkxNC01YjM2LTQ4OGQtOTcyMy1iMDAxMjA2M2Q2OTIiLCJleHAiOjE0MjE4NzY5MTYsIm5iZiI6MCwiaWF0IjoxNDIxODc2NjE2LCJpc3MiOiJvYXV0aDItdGVzdCIsImF1ZCI6Im9hdXRoLWFwaS1jbGllbnQiLCJzdWIiOiJkY2VmZjc2ZC0xNjAzLTQ3NGQtOTViNi0zYjc3YzlhNTEzZGMiLCJhenAiOiJvYXV0aC1hcGktY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjU0M2MxMWQ4LWRlYjQtNDE4NC1iYzA0LWMxZmZiZTRjOGQxZiIsImFsbG93ZWQtb3JpZ2lucyI6W10sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibWljbG91ZGh5c3RyaXgiOnsicm9sZXMiOlsidXNlciJdfSwibWljbG91ZHJlc3RiYXNlIjp7InJvbGVzIjpbIlJFQUQiLCJXUklURSJdfX19.Sc3ivDPrsDiEWfDuQIz44iZ0hLwO3Vxc9xfI_asZCgUCS-btrAmGBiDyrFbXbUgV9dQRv84OBElBXvVK97B3GfAFv6olOesENbK3prWEP-P43RV_qBbY3Pbs9YygtAB9PC5IgN8TqUy73pI3fcifdCDuUELylXnTClCa9-Vv4gw" -XGET "http://127.0.0.1:8089/devices/search/findByModel?model=MontaVista"```

easy to user version :

```curl -v -H "Authorization: Bearer <token here>" -XGET "http://127.0.0.1:8089/devices/search/findByModel?model=MontaVista"```

---

End. :)