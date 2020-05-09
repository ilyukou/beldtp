# The bot was created to fill the public database of road accidents in the Republic of Belarus.

#### Why is it needed?

A publicly accessible incident database will help officials, road designers, and all city activists researching road safety to quickly locate accidents, thanks to convenient and quick access to accident data, filtering them by time, location.

#### How to work with her?

You can add incidents to it through a [telegram bot](https://t.me/beldtp_bot). You can get information about all sent incidents via the [API](https://github.com/ilyukou/beldtp-api) or by downloading them from the [cloud storage](https://github.com/ilyukou/beldtp-api/blob/master/docs/Drive.md), also each new sent incident will be published in the [channel](https://t.me/beldtp)

#### Why exactly our option?

##### No analogues

Currently, there is no way to quickly obtain information about accidents in a single place and filter them by location and time.

##### Convenience

The process of sending an accident does not exceed one minute. The bot does not overload the process of creating an application with unnecessary movements. You are required to add: a description of the incident, the time when it happened, the place, as well as documentary evidence (photo, video or document).

##### Open source code

Anyone can contribute to the development of the project or draw on the idea for their projects.

##### Open and free API

Allows anyone to receive information about all sent incidents.

You can also get information only for the selected area, for a certain period of time, or for other parameters. Full instructions for working with the [API](https://github.com/ilyukou/beldtp-api)

##### Simplicity

Not sure what API is and how to work with it? No problem. From time to time we upload all incidents to the [cloud storage](https://github.com/ilyukou/bedltp-api/blob/master/docs/Drive.md), where you can download the full archive by clicking on one button.

##### Ability to add "retroactively"

The bot will allow you to add information not only about new cases, but also about past events, which will help to monitor changes in safety on the roads of Belarus at certain intervals.

------

Bot deployed at [http://t.me/beldtp_bot](http://t.me/beldtp_bot)

[Instructions for using the bot](https://github.com/ilyukou/beldtp/tree/master/docs/Help.md)

[Instructions for the developer](https://github.com/ilyukou/beldtp/tree/master/docs/Dev.md)

You can get information about all sent incidents through [API](https://github.com/ilyukou/beldtp-api) or download them from [cloud storage](https://github.com/ilyukou/beldtp-api/tree/master/docs/Drive.md). 

Each new incident will be published in the [channel](http://t.me/beldtp)

[Chat](http://t.me/beldtp_chat) on traffic accidents, traffic violations in Belarus

[Author](http://t.me/ilyukou)

___
### Change log
#### v0.1.6
* All new files are automatically public for reading.
* Auto generate backup file and push to S3 ( schedule a hour )

#### v0.1.5
* All urls wrapping in HTML format for support [UI Map](https://ilyukou.github.io/beldtp-map/)
* fix bug : When incident delete, incident not deleted

#### v0.1.4
* Fix bugs when user send files without compression

#### v0.1.3

* All urls wrapping in Markdown format
* fix bugs

#### v0.1.2 

* add support http(s) url's
* fix bugs

#### v0.1.1 

* add guava cache for storing Answers
* fix java.lang.StackOverflowError
* add Docs

#### v0.1 

* First Beta version