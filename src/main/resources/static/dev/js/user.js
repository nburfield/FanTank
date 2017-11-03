
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
      $(".nouser").hide();
      $(".userName").html(user.firstName);
    }
    else {
      $(".nouser").show();
      $(".user").hide()
    }
  })
  .fail(function(data) {
    $(".nouser").show();
    $(".user").hide()
  });
}

function logout() {
  $.get(serverContext + "/csrf", function(csrf) {
    $.ajaxSetup({
      headers: {
        'X-CSRF-TOKEN': csrf.token
      }
    });
    //var sendData = { csrf.parameterName: csrf.token };
    $.post(serverContext + "/logout", {}, function(data) {
      loadUser();
    })
    .fail(function(data) {
      console.log("Logout Failed")
    });
  })
  .fail(function(data) {
    console.log("CSRF Failure")
  });
}
