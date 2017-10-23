var daysLeft = 0;
var hoursLeft = 0;
var minutesLeft = 0;
var asking = 0;
var progress = 0;
var recieved= 0;

$(document).ready(function () {
  $(".offering").each(function(index, event) {
    if(event.getAttribute('id') != null) {
      loadOfferingData($("#" + event.getAttribute('id')), event.getAttribute('id'), false);
    }
  });
});
