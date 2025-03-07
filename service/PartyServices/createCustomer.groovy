
import java.sql.Timestamp
import java.time.ZonedDateTime
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind

Map<Object,Object> servicecall=ec.service.sync().name("PartyServices.find#Customer").parameters(context).call()
//ExecutionContext ec = context.ec
//EntityFind ef = ec.entity.find("assignment.FindCustomerView").distinct(true)
//ef.condition(
//        ec.entity.conditionFactory.makeCondition("infoString", EntityCondition.EQUALS, emailAddress).ignoreCase()
//)
//count=ef.count()
if(servicecall.partyIdList)
{
    ec.message.addError("Customer Already Exists")
    return
}
if (context.emailAddress==null||context.firstName==null||context.lastName==null) {
    ec.message.addError("please enter all required fields")
    return
}
def party = ec.entity.makeValue("assignment.Party")

Map<String, Object> fields = [
        "partyTypeEnumId": "PtyPerson",
]
party.setAll(fields)
party.setSequencedIdPrimary()
party = party.create()

def person = ec.entity.makeValue("assignment.Person")
fields = [
        "partyId":party.get("partyId"),
        "firstName": context.firstName,
        "lastName": context.lastName
]
person.setAll(fields)
person = person.create()

def partyRole = ec.entity.makeValue("assignment.PartyRole")
fields = [
        "partyId":party.get("partyId"),
        "roleTypeId": "Customer"
]
partyRole.setAll(fields)
partyRole = partyRole.create()

def contactMech= ec.entity.makeValue("assignment.contact.ContactMech")
fields = [
        "contactMechTypeEnumId":"CmtGeneralEmail",
        "infoString": context.emailAddress
]
contactMech.setAll(fields)
contactMech.setSequencedIdPrimary()
contactMech = contactMech.create()

ZonedDateTime now = ZonedDateTime.now()
long nowMillis = now.toInstant().toEpochMilli()
Timestamp nowTimestamp = new Timestamp(nowMillis)

def partyContactMech = ec.entity.makeValue("assignment.contact.PartyContactMech")
fields = [
        "partyId":party.get("partyId"),
        "contactMechId": contactMech.get("contactMechId"),
        "contactMechPurposeId":"GeneralEmail",
        "fromDate":nowTimestamp.toString()
]
partyContactMech.setAll(fields)
partyContactMech = partyContactMech.create()


context.partyId = party.get("partyId")
