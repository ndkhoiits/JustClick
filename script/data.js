var currentUser;
var json;

// Send a request to get viewer id
function request() {
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), "get_viewer");
  req.send(response);
}

function response(dataResponse) {
  var owner = dataResponse.get('get_viewer').getData();
  currentUser = "khoi_nguyen";
  reloadData(currentUser);
}

// Get the data in rest service
function reloadData(userId) {
  currentUser = userId;
  console.log(currentUser);
  getContacts();
}

function getContacts(rest) {
  var currentView = gadgets.views.getCurrentView().getName();
  console.log(currentView);
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
