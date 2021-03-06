Features
===========

 - Send E-mail


###Send E-mail

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus-utils/v1/emails |
| Auth | Required |
| Request Body Type | JSON |
| Response Code | 200 Successful |

**Request body fields**

| Name | command |
|------------------------|--------|
| mail_template_name | either mail_template_name or mail_template_content should exists in request body json. |
| mail_template_context | template  placeholder replace value, should in JSON format. |
| to_addresses | It should contains recepitent e-mail address. |
| cc_addresses | It should contains carbon copy e-mail address. |
| attachments  | It can have file attachment urls and names. |
| mail_subject | If mail_template_name is mentioned, no need to set this in request json.|
| mail_template_content | If mail_template_name is mentioned, no need to set this in request json.|

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
curl -i  -H "Content-Type: application/json" -H "Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2" -X POST -d '{
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
}' http://nucleus.gooru.org/api/nucleus-utils/v1/emails
```


**Sample 2**

**Response body**

```json
  {
    "mail_subject": "welcome to the classical world!!!",
    "mail_template_content": "Awake up from dream, we are already in classical world :)",
    "to_addresses": [
        "sheeban@gooru.org",
        "parthiban@gooru.org"
    ],
    "cc_addresses": [
        "daniel@gooru.org"
    ]
  }
```


**CURL snippet**

```posh
curl -i  -H "Content-Type: application/json" -H "Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2" -X POST -d '{
    "mail_subject": "welcome to the classical world!!!",
    "mail_template_content": "Awake up from dream, we are already in classical world :)",
    "to_addresses": [
        "sheeban@gooru.org",
        "parthiban@gooru.org"
    ],
    "cc_addresses": [
        "daniel@gooru.org"
    ]
}' http://nucleus.gooru.org/api/nucleus-utils/v1/emails
```

