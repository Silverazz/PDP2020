## Prepare Your Environment

### Register Your App
You will need to [ register your application](https://developer.spotify.com/dashboard/login) on the [Developer Dashboard](https://developer.spotify.com/dashboard/) and obtain a client ID. When you register your app you will also need to whitelist a redirect URI that the Spotify Accounts Service will use to callback to your app after authorization. You also should add your package name and app fingerprint as they’re used to verify the identity of your application.

### Register App Fingerprints
Fingerprints are used for authentication between your Android Application and the Spotify service. You’ll need to generate a fingerprint for your app and register it in your Dashboard. We strongly recommend that you create both a development and a production fingerprint for security reasons.

#### To Create a Development Fingerprint

1.  Run the following command in your Terminal (no password):
`On Bash style shells`
`$ keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v | grep SHA1`
`On Windows Powershell`
`$ keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore -list -v | grep SHA1`

You should expect to receive a fingerprint that looks like this: `SHA1: E7:47:B5:45:71:A9:B4:47:EA:AD:21:D7:7C:A2:8D:B4:89:1C:BF:75`

2. Copy the fingerprint and your package name and enter it in the Spotify Developer Dashboard. Don’t forget to click Save after you added the fingerprints in the dashboard.
![AndroidPackages](https://developer.spotify.com/assets/AndroidPackages.png)
