
var serverContext = "http://localhost:3000/";
//serverContext = "http://fantank.bcinnovationsonline.com/";

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
