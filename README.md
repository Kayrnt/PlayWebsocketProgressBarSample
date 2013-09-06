Play Websocket Progress Bar Sample
==============================

A sample of use of WebSocket with Play Framework 2 using Scala to make a progress bar. The sample also makes use of AngularJS and Akka. 

Base example shows the simple way without Akka.

Akka example shows a bit more advanced example using a process started in the background. Progression isn't "destroyed" when you refresh the page thanks to Akka actors.
Pressing "reset" will clear the session and reset the processing for your client. You can refresh the page but this demo only handles one "client" at the same time.
Opening 2 tabs in progress will "break" the progress bar for the first client. It isn't hard to "fix" but it would add unnecessary complexity to the project.

It might be a bit hacky so if you want to clean it up, you can send pull requests.

Using it
--------

* Download [the latest playframework version] (http://www.playframework.com/). The version 2.1.3 is used in this sample.
* Run this application with it ('play run' in the root directory) 
* Go to [http://localhost:9000/](http://localhost:9000/)

Libraries used
--------
* [Play Framework] (http://www.playframework.com/)
* [Akka] (http://akka.io/)
* [AngularJS] (http://angularjs.org/)
* [Bootstrap] (http://getbootstrap.com/)
* [JQuery] (http://jquery.com/)

Contact Me
--------

* [@Kayrnt](https://twitter.com/Kayrnt)
* [Porfolio](http://www.kayrnt.fr)

Licence
-------

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
