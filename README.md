# backend

How to project with docker:
- Install WSL and Docker Desktop (windows) or docker (linux)
- install Intelij 
- Configure Docker run in intelij: ![image](https://user-images.githubusercontent.com/41972182/231864467-d13fe888-f463-4278-aa95-eb1821253c74.png)
  Nessesary envs are provided on our Discord
- If you want to have access to local database (it is created by docker automatically) configure data soruce in Intelij ![image](https://user-images.githubusercontent.com/41972182/231864870-d103bb5f-636a-45fc-9e02-6626d6395f38.png)
  password is as in envs on our Discord

Now you should be able to just click run ![image](https://user-images.githubusercontent.com/41972182/231864998-529c4ba9-3fc3-4019-90bb-61b1ff04143e.png)
and everything should work fine. REMEMBER: our backend is now on 5000 not 8080!

Issues I found:
 - I don't know yet how to debug docker image
 - I think database doesn't persist data, or rather what cloesed/killed it just losese it (though theoretically it should not happen)
