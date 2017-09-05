
var serverContext = window.location.protocol + "//" + window.location.host + "/";

var userLogged = null;
var daysLeft = 0
var hoursLeft = 0
var minutesLeft = 0
var asking = 0
var progress = 0

console.log("Calling my");

// Invest Now Attributes
var updateEvent = document.createEvent('Event');
var autofillEvent = document.createEvent('Event');
var clearEvent = document.createEvent('Event');
clearEvent.initEvent('fa.investnow.clear', true, false);
var bindButtonsEvent = document.createEvent('Event');
bindButtonsEvent.initEvent('fa.investnow.bindButtons', true, false);

var getDataEvent = document.createEvent('Event');
getDataEvent.initEvent('fa.investnow.getData', true, false);

document.addEventListener('fa.investnow.open', function(e) {
  console.log("InvestNow Open")
  document.body.dispatchEvent(autofillEvent);
  document.body.dispatchEvent(getDataEvent);
});

// document.addEventListener('fa.investnow.success', function(e){
//   var InvestorInvestmentID = e.investment_id;
//   var userInvestment = {
//         userId: userLogged.id,
//         investmentId: InvestorInvestmentID
//       }
//   sendUserInvestment(userInvestment);
// });

$(document).ready(function () {
  $(".user").hide();
  $(".nouser").hide();
  loadUser();
  loadOfferingData($('#invest_now_panel'), window.location.pathname.split('/')[2]);
});

function loadUser() {
  $.get(serverContext + "/user/data", function(user) {
    if(user) {
      $(".user").show()
      $(".userName").html(user.firstName);
      userLogged = user;
      setInvestNowData(user);
      autoFillData(user);
      document.body.dispatchEvent(bindButtonsEvent);
    }
    else {
      $(".nouser").show();
    }
  })
  .fail(function(data) {
    $(".nouser").show();
  });
}

function sendUserInvestment(userInvestment) {
  $.post(serverContext + "/user/investment", userInvestment, function(data) {
    $.notify("Investment Connection Success", { position:"right bottom", className: "success" });
  })
  .fail(function(data) {
    $.notify(data.responseJSON.message, { position:"right bottom", className: "error" });
  });
}

function setInvestNowData(user) {
  updateEvent.initEvent('fa.investnow.update', true, false);
  updateEvent.detail = {
    "email":  user.email,
    "id" : user.id
  };
  try {
    document.body.dispatchEvent(clearEvent);
    document.body.dispatchEvent(updateEvent);
  }
  catch(err) {
    console.error("Updated Event Error")
  }
}

function autoFillData(user) {
  autofillEvent.initEvent('fa.investnow.autofill', true, false);
  autofillEvent.investor = {
        "name": user.firstName + " " + user.lastName,
        "email": user.email,
        "contact_name": user.firstName + " " + user.lastName
      };
  autofillEvent.associated_person = {
    "name": user.firstName + " " + user.lastName,
    "email": user.email
  };
}
