function loadOfferingData(element, offeringId) {
  $.get(serverContext + "/offerings/" + offeringId, function(offering) {
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
      daysLeft = 0;
      $.notify("Escrow closed on Offering", { position:"right bottom", className: "error" });
    }

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

    element.find('.progress-bar').css('width', progress + '%').attr('aria-valuenow', progress);

    element.find('.offeringPercent').animateNumber(
      {
        number: progress,
      },
      2000
    );

    var comma_step = $.animateNumber.numberStepFactories.separator(',')
    element.find('.offeringAsking').animateNumber(
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
