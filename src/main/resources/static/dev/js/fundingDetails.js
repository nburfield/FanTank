function loadOfferingData(element, offeringId, alertClosed) {
  $.get(serverContext + "/offerings/" + offeringId, function(offering) {
    // Get the days left on escrow
    var now = moment();
    var closeDate = moment(offering.escrow_closes_at);
    daysLeft = closeDate.diff(now, 'days');
    hoursLeft = closeDate.diff(now, 'hours');
    minutesLeft = closeDate.diff(now, 'minutes');

    // Get the progress of the funding
    recieved = parseFloat(offering.funds_received);
    asking = parseFloat(offering.amount);
    progress = Math.ceil((recieved / asking) * 100);
  })
  .done(function(data) {
    // Set the HTML attributes
    if(minutesLeft < 2) {
      minutesLeft = 3000;
      if(alertClosed) {
        $.notify("Escrow closed on Offering", { position:"right bottom", className: "error" });
      }
    }

    element.find('.offeringDayHour').html("Days");
    element.find('.offeringDaysLeft')
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
            element.find('.offeringDayHour').html("Hours");
            element.find('.offeringDaysLeft')
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
                    element.find('.offeringDayHour').html("Minutes");
                    element.find('.offeringDaysLeft')
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

    // No animate time
    if(minutesLeft < 120) {
      element.find('.offeringDayHourNoAnimate').html("Minutes");
      element.find('.offeringDaysLeftNoAnimate').html(minutesLeft);
    }
    else if(minutesLeft < 2880) {
      element.find('.offeringDayHourNoAnimate').html("Hours");
      element.find('.offeringDaysLeftNoAnimate').html(hoursLeft)
    }
    else {
      element.find('.offeringDayHourNoAnimate').html("Days");
      element.find('.offeringDaysLeftNoAnimate').html(daysLeft);
    }

    element.find('.progress-bar').css('width', progress + '%').attr('aria-valuenow', progress);

    element.find('.offeringPercent').animateNumber(
      {
        number: progress,
      },
      2000
    );
    element.find('.offeringPercentNoAnimate').html(progress);
    element.find('.offeringRecievedNoAnimate').number(recieved);

    var comma_step = $.animateNumber.numberStepFactories.separator(',')
    element.find('.offeringAsking').animateNumber(
      {
        number: asking,
        numberStep: comma_step
      },
      2000
    );
    element.find('.offeringAskingNoAnimate').number(asking);
  })
  .fail(function(data) {
    element.find('.offeringDaysLeft').html(0);
    element.find('.offeringAsking').html(0);
    element.find('.offeringPercent').html(0);
    element.find('.progress-bar').css('width', 0 + '%').attr('aria-valuenow', 0);
  });
}
