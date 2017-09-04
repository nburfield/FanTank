
var serverContext = window.location.protocol + "//" + window.location.host + "/";

$(document).ready(function () {
  $(".user").hide();
  $(".nouser").hide();
  loadUser();
});

function loadUser(){
  $.get(serverContext + "/user/data", function(user) {
    if(user) {
      $(".user").show()
      $(".userName").html(user.firstName);
    }
    else {
      $(".nouser").show();
    }
  })
  .fail(function(data) {
    $(".nouser").show();
  });
}

