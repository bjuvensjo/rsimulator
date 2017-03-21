import groovy.xml.StreamingMarkupBuilder;

def simulatorResponseOptional = vars.get("simulatorResponseOptional")

def request = new XmlSlurper().parseText(vars.get("simulatorRequest"))
def simulatorResponse = simulatorResponseOptional.get()
def response = new XmlSlurper().parseText(simulatorResponse.response)

def amount = request.Body.TransferRequest.transfer.amount.text().toDouble()

response.Body.TransferResponse.Receipt.from.balance = request.Body.TransferRequest.transfer.from.balance.text().toDouble() - amount
response.Body.TransferResponse.Receipt.to.balance = request.Body.TransferRequest.transfer.to.balance.text().toDouble() + amount

simulatorResponse.response = new StreamingMarkupBuilder().bind {
    out << response
}

