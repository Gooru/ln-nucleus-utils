USER ERROR API
===========

###User Error

**Rest API details**

| Name | Summary |
|------------------------|--------|
| HTTP Method | POST |
| End Point | /api/nucleus-utils/v1/user-error |
| Auth | Required (Anonymous) |
| Request Body Type | JSON |
| Response Code | 200 Successful |

**Request body fields**

| Name | command |
|------------------------|--------|
| client_timestamp | Timestamp of the error. Whatever format is suitable for FE is fine |
| user_id | logged in user id or anonymous |
| log_type | ERROR or WARN |
| client_info | Browser or such information that may be helpful to reproduce or debug |
| client_context  | page or url or route |
| api | If error is due to the API, otherwise null |
| api_status | API return status code, if not due to the API then null |
| api_response | API response if any, if not due to the API then null |
| message | any other message FE wants to log for troubleshooting purpose |

**Authorization Header**

```
Authorization: Token MTQ1NTc5OTI3NDU0Mzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2
```

**Request body**

```json
  {
	"client_timestamp": "2016-07-27 12:04:04.605",
	"user_id": "4ee8c715-60cb-42d2-8387-fa425a4f708a",
	"log_type": "ERROR",
	"client_info": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36",
	"client_context": "page",
	"api": "http://nucleus-qa.gooru.org/api/nucleus/v1/courses",
	"api_status": 503,
	"api_response": null,
	"message": "this is test"
}
```


**CURL snippet**

```posh
curl -X POST -H "Content-Type: application/json" -H "Authorization: Token MTQ2OTYwOTA2NDY1MDphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2" -d '{ "client_timestamp": "2016-07-27 12:04:04.605", "user_id": "4ee8c715-60cb-42d2-8387-fa425a4f708a", "log_type": "ERROR", "client_info": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36", "client_context": "page", "api": "http://nucleus-qa.gooru.org/api/nucleus/v1/courses", "api_status": 503, "api_response": null, "message": "this is test"}' "http://nucleus-qa.gooru.org/api/nucleus-utils/v1/user-error"
```
