# IFTTT + Google Assistant + Sony Android TV = TV Voice Commands!

### An example REST API to control your Sony Android TV with remote commands like play, pause, and volumeup using IFTTT and Google Assistant or even Alexa!

#### This project serves 2 purposes:

1. Expose a REST resource for Voice commands to a Sony Android TV. `HTTP :8080/api/command command=netflix`
2. Gateway/Proxy access to apps like NZBGet, Sonarr, and Radarr with SSO via Auth0/Github OAuth and support basic authentication in one unified place.

_Note: This should really be two separate apps but I'm lazy and its nice to deploy a single Docker image to my server for all my needs._


## Building

_The project can be deployed as a runnable Spring Boot jar or Docker container._

`./mvnw clean install spring-boot:run`

or

`docker-compose up` and available as `jamorin/spring-boot-sony-tv-rest:latest`

### Okay, so what?

You can set up IFTTT with Google Assistant with IFTTT's Webhooks channel to issue simple voice commands to your Android TV from any Google Assistant supported device.

### Play/Pause Voice Control with IFTTT

**IF** You say `OK Google, pause the TV` or `OK Google, resume the TV`
 
**THEN** make a web hook request to `https://user:password@sub.domain.com:8443/api/command`
```
Method: POST
Content-Type: application/json
Body: {"command":"pause"}
```

This will invert the playing status of the TV.

### Voice Control with variable `{{TextField}}` input

**IF** You say `OK Google, open $, TV $, switch to $]` (where $ can be something like pause, play, enter, input1, netflix, volumeup, etc.. any function on your remote)
 
**THEN** make a web request to `https://user:password@sub.domain.com:8443/api/command`
```
Method: POST
Content-Type: application/json
Body: {"command":"{{TextField}}"}
```

{{TextField}} will be injected by IFTTT with your selection and sent as a remote control command.

You can make as many custom options as you like. Unknown commands will just log an error.

See `tv.*` properties in application.properties for example setup

## SSO Gateway and Basic Auth for Radarr, Sonarr, and NZBGet + Others

Usenet apps like Radarr, Sonarr, and NZBGet only support basic auth and form login. This proxy allows you to use Auth0/Github SSO instead!

Note: for Auth0 make sure you give them acess by setting `app_metadata` for each user to this json:

`{
   "authorities": [ "ROLE_API"]
 }
 `

This app is meant to be the public gateway to the other apps, all security is handled here. So, be sure to disable authentication and public access on those apps directly.

Currently, accessing /sonarr, /radarr, /nzbget will require a whitelisted Github user.

Once authenticated, you will be allowed through to the apps or denied if not whitelisted.

See the `proxy.*` and `security.*` properties in application.properties for example setup.

`/api/**` routes will use basic auth so application clients like NZB360 can still proxy through.

Thanks for looking at this stuff.