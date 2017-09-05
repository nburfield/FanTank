
$(document).ready(function () {
  $(".offering").each(function(index, event) {
    console.log(event.getAttribute('id'));
    if(event.getAttribute('id') != null) {
      loadOfferingData($("#" + event.getAttribute('id')), event.getAttribute('id'));
    }
  });
});
