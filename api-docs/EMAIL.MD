Features
===========

 - Send E-mail

###Send E-mail.

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus-utils/emails |
| Auth | Required |
| Request Body Type | None |
| Request Body  | None |
| Response Code | 200 Successful |

**Request body fields**
| Name | command |
|------------------------|--------|
| mail_template_name | either mail_template_name or mail_template_content should exists in json |
| mail_template_context | template  placeholder replace value, should in JSON format |
| to_addresses | It should contains recepitent e-mail address |
| cc_addresses | It should contains carbon copy e-mail address |
| attachments  |  |
| mail_template_subject |  |


**Authorization Header**

```
Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2
```

**Sample 1**

**Response body**

```json
  {
    "mail_template_name": "welcome_mail",
    "to_addresses": [
        "sheeban@gooru.org"
    ],
    "attachments": [
        {
            "url": "http://www.cbu.edu.zm/downloads/pdf-sample.pdf",
            "filename": "attachment.pdf"
        }
    ]
  }
```


**CURL snippet**

```posh
curl -i  -H "Content-Type: application/json" -H "Authorization: Token curl -i  -H "Content-Type: application/json" -H "Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2" -X POST -d '{
    "mail_template_name": "welcome_mail",
    "to_addresses": [
        "sheeban@gooru.org"
    ],
    "attachments": [
        {
            "url": "http://www.cbu.edu.zm/downloads/pdf-sample.pdf",
            "filename": "attachment.pdf"
        }
    ]
}' http://127.0.0.1:8080/api/nucleus-utils/v1/emails
```


**Sample 2**

**Response body**

```json
  {
    "mail_template_name": "welcome_mail",
    "to_addresses": [
        "sheeban@gooru.org"
    ],
    "attachments": [
        {
            "url": "http://www.cbu.edu.zm/downloads/pdf-sample.pdf",
            "filename": "attachment.pdf"
        }
    ]
  }
```


**CURL snippet**

```posh
curl -i  -H "Content-Type: application/json" -H "Authorization: Token curl -i  -H "Content-Type: application/json" -H "Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2" -X POST -d '{
    "mail_template_name": "welcome_mail",
    "to_addresses": [
        "sheeban@gooru.org"
    ],
    "attachments": [
        {
            "url": "http://www.cbu.edu.zm/downloads/pdf-sample.pdf",
            "filename": "attachment.pdf"
        }
    ]
}' http://127.0.0.1:8080/api/nucleus-utils/v1/emails
```

