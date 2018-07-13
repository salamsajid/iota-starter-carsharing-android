# IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app for Android


## Overview
The IBM IoT Connected Vehicle Insights - Mobility Starter Application uses the **Context Mapping** and **Driver Behavior** services that are available on **IBM Cloud** to help you to quickly build a smart car-sharing automotive solution. The IBM IoT Connected Vehicle Insights - Mobility Starter Application consists of a mobile app and a server component.

### Mobile app
The starter app provides a mobile app for customers to quickly find and hire a car without human intervention from a car-hire company. If you are a customer who wants to hire a car, you can use the mobile app to do the following tasks:

- Search for available cars by location on a GIS map
- Search for available cars that meet your specific requirements
- Reserve a car 
- Unlock the car that you just hired and start driving the car
 
While you drive the car, the service tracks your location and also records your driving behavior. When you reach your driving destination, you can view information about each trip that you took in the car and you can also view your driving behavior score.

You can download and install the mobile app on iOS and Android mobile devices. For more information about deploying the iOS version of the mobile app, see [IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app for iOS](https://github.com/ibm-watson-iot/iota-starter-carsharing/blob/master/README.md).

### Server component
The IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app interacts with the server component. The server component provides the back-end car sharing and system monitoring service that provides more features for car-hire companies. By default, the mobile app connects to a test server that is provided by IBM. You can also choose to deploy your own server instance to IBM Cloud and connect your mobile app to that instance instead of the test system. For more information about deploying the car-sharing server component, see [ibm-watson-iot/iota-starter-server](https://github.com/ibm-watson-iot/iota-starter-server).


## Prerequisites

Before you deploy the IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app for Android, ensure that the following prerequisites are met:

- Install the Android Studio integrated development environment (IDE) V2.1.1 or later.
- Install an Android emulator device that is running on at least API Level 21.
- The sample source code for the mobile app is supported only for use with an Android device and is intended to be used in conjunction with officially licensed Android development tools and further customized tools that are distributed under the terms and conditions of your licensed Android Developer Program.


## Deploying the mobile app

You can download and install the mobile app on iOS and Android mobile devices. For more information about trying the iOS version of the mobile app, see [IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app for iOS](https://github.com/ibm-watson-iot/iota-starter-carsharing).

To try the IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app for Android with Android Emulator, complete the following steps:

1. Clone the Mobility Starter Application source code repository for the sample mobile app by using the following git command:    

    ```$ git clone https://github.com/ibm-watson-iot/iota-starter-carsharing-android```  

2. Open the project in Android Studio.

    If you encounter the following dialog, press 'OK'.    
     
    ![Gradle Sync Dialog](GradleSync.jpg)    
     
3. Edit the **iota-starter-carsharing-android/app/java/carsharing.starter.automotive.iot.ibm.com.mobilestarterapp/ConnectedDriverAPI/API.java** file, and set the `connectedAppURL` variable to the URL for your IBM IoT Connected Vehicle Insights - Mobility Starter Application server app.
4. Edit the **iota-starter-carsharing-android/app/res/values/google_maps_api.xml** file and replace `YOUR_KEY_HERE` with your `google_maps_key`. For more information, see [Get API Key](https://developers.google.com/maps/documentation/android-api/signup).

5. In Android Studio, run the application by pressing **Run 'app'**.

    If you use a virtual device, make sure that you use one with API level 21 or later as seen in the following dialog.

    ![Deployment Target](DeploymentTarget.jpg)

6. To deploy the mobile app on your device, see [Build and Run Your App](https://developer.android.com/studio/run/index.html).

## Reporting defects
To report a defect with the IBM IoT Connected Vehicle Insights - Mobility Starter Application mobile app, go to the [Issues](https://github.com/ibm-watson-iot/iota-starter-carsharing-android/issues) section.

## Privacy notice
The IBM IoT Connected Vehicle Insights - Mobility Starter Application on IBM Cloud stores all of the driving data that is obtained while you use the mobile app.

## Questions, comments or suggestions
For your questions, comments or suggestions to us, visit [IBM IoT Connected Vehicle Insights Application community site] (https://www.ibm.com/developerworks/community/groups/service/html/communitystart?communityUuid=3b06ca1c-fd7c-4a59-a888-e5e3a8384091).

## Useful links

- [IBM IoT Connected Vehicle Insights](http://www.ibm.com/internet-of-things/iot-industry/iot-automotive)
- [IBM Watson Internet of Things](http://www.ibm.com/internet-of-things/)  
- [IBM Watson IoT Platform](http://www.ibm.com/internet-of-things/iot-solutions/watson-iot-platform/)   
- [IBM Watson IoT Platform Developers Community](https://developer.ibm.com/iotplatform/)
- [IBM Cloud](https://bluemix.net/)  
- [IBM Cloud documentation](https://www.ng.bluemix.net/docs/)  
- [IBM Cloud developers community](http://developer.ibm.com/bluemix) 
