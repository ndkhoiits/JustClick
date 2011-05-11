var json;

// Send a request to get viewer id
function request() {
  var opts = {};
  opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
    opensocial.Person.Field.PROFILE_URL,
    opensocial.Person.Field.THUMBNAIL_URL];
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, opts), "viewer");
  req.send(response);
}

function response(dataResponse) {
  this.viewer = dataResponse.get('viewer').getData();
  var profileTempUrl = this.viewer.getField(opensocial.Person.Field.PROFILE_URL);
  var username = profileTempUrl.substr(profileTempUrl.lastIndexOf('/') + 1)
  reloadData(username);
}

// Get the data in rest service
function reloadData(userId) {
  currentUser = userId;
  getContacts();
}

function getContacts(rest) {
  var currentView = gadgets.views.getCurrentView().getName();
  var sendData = "username=" + currentUser;
  var callback = responseData;
  return $.ajax({
    url: "/rest/my-service/getService",
    type: "GET",
    async: false,
    success: callback,
    contentType: "application/x-www-form-urlencoded",
    error: function() {$("#PageList").html("Cannot load Page data from service!");},
    data: sendData,
    dataType: "json"
  });
}

function responseData(data) {
  json = data;
  init();
}
