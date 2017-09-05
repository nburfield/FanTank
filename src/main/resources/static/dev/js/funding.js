
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
  loadOfferingData(window.location.pathname.split('/')[2]);
});

function loadOfferingData(offeringId) {
  $.get(serverContext + "/offerings/" + offeringId, function(offering) {
    console.log(offering);

    // Get the days left on escrow
    var now = moment();
    var closeDate = moment(offering.escrow_closes_at);
    daysLeft = closeDate.diff(now, 'days');
    hoursLeft = closeDate.diff(now, 'hours');
    minutesLeft = closeDate.diff(now, 'minutes');

    // Get the progress of the funding
    var recieved = parseFloat(offering.funds_received);
    asking = parseFloat(offering.amount);
    progress = Math.ceil((recieved / asking) * 100);
  })
  .done(function(data) {
    // Set the HTML attributes
    if(minutesLeft < 2) {
      minutesLeft = 3000;
      $.notify("Escrow closed on Offering", "error");
    }


    $('.offeringDaysLeft').html(hoursLeft);
    $('.offeringDayHour').html("Days");
    $('.offeringDaysLeft')
      .prop('number', 90)
      .animateNumber(
        {
          number: daysLeft,
          numberStep: function(now, tween) {
            var target = $(tween.elem),
                rounded_now = Math.round(now);
            if(rounded_now < 4) {
              $('.days-left').css('color', 'red');
            }
            target.text(rounded_now);
          }
        },
        1500,
        function() {
          if(minutesLeft < 2880) {
            $('.offeringDayHour').html("Hours");
            $('.offeringDaysLeft')
              .prop('number', 48)
              .animateNumber(
                {
                  number: hoursLeft,
                  numberStep: function(now, tween) {
                    var target = $(tween.elem),
                        rounded_now = Math.round(now);

                    target.text(rounded_now);
                  }
                },
                500,
                function() {
                  if(minutesLeft < 120) {
                    $('.offeringDayHour').html("Minutes");
                    $('.offeringDaysLeft')
                      .prop('number', 120)
                      .animateNumber(
                        {
                          number: minutesLeft,
                          numberStep: function(now, tween) {
                            var target = $(tween.elem),
                                rounded_now = Math.round(now);

                            target.text(rounded_now);
                          }
                        },
                        500
                      );
                  }
                }
              );
          }
        }
      );

    $('.progress-bar').css('width', progress + '%').attr('aria-valuenow', progress);

    $('.offeringPercent').animateNumber(
      {
        number: progress,
      },
      2000
    );

    var comma_step = $.animateNumber.numberStepFactories.separator(',')
    $('.offeringAsking').animateNumber(
      {
        number: asking,
        numberStep: comma_step
      },
      2000
    );
  })
  .fail(function(data) {
    $('.offeringDaysLeft').html(0);
    $('.offeringAsking').html(0);
    $('.offeringPercent').html(0);
    $('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
  });
}

function sendUserInvestment(userInvestment) {
  $.post(serverContext + "/user/investment", userInvestment, function(data) {
    $.notify("Investment Connection Success", "success");
  })
  .fail(function(data) {
    $.notify(data.responseJSON.message, "error");
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
