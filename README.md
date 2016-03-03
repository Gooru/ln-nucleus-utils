nucleus-utils
==============

These are utility components for nucleus. They contain one HTTP server and that way they qualify as another cluster in nucleus infrastructure.

Current target is to make sure that we are providing an email notifier which works off templates to send out external emails.



### Mail configuration

- In ```nucleus-utils.json``` conf file  which as the ```mail.config.properties``` key and it has the values of mail properties details, if it requires any changes we can update  it.

- In ```nucleus-utils.json``` conf file  which as the ```mail.auth.properties``` key and it has the  values of mail service provider authentication credentials. Replace the place holder value of [USERNAME] and [PASSWORD] with the actual value before starting the service.

### Mail templates

- All the email templates should be store in project src/main/resources/mail-templates folder.

- The e-mail subject details should be add/update in ```mail-subjects.properties```.

- Template file extension should be ```.vm```.


To understand build related stuff, take a look at **BUILD_README.md**.
