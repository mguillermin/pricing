$(document).ready(function() {
	$("#toggle-edition").live('click', function(){
		$("ul.actions").toggle();
	});
	$("#pricingtag-edit h4").live('click', function(){
		$(this).parent().children('form').slideToggle();
	}).css('cursor', 'pointer');
	$("#pricingtag-edit form").hide();
});

// Section inserting
$("#pricing-section-add").live('click', function(){
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      // Inserting resulting line before the total one
      $("#pricing tr.total").before(data);
      $("#pricing tr.total").prev().effect('highlight');
      bindEditables();
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});

// Line inserting
$(".line-add").live('click', function(){
  var el = $(this);
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      // Inserting resulting line before the next section or before the total line (for the last one)
      var matched = el.parents("tr.section").nextAll("tr.section, tr.total").first();
      matched.before(data);
      matched.prev().effect('highlight');
      bindEditables();
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});

// Section deleting
$(".section-delete").live('click', function() {
  var el = $(this);
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      // Select the section tr
      var section = el.parents("tr.section");
      // Select everything between this section and the other (or tr.total for managing last section)
      var lines = section.nextUntil("tr.section, tr.total");
      section.remove();
      lines.remove();
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});

// Line deleting
$(".line-delete").live('click', function() {
  var el = $(this);
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      el.parents("tr.line").remove();
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});

// Section up'ing / down'ing
$(".section-up, .section-down").live('click', function() {
  var el = $(this);
  var up = $(this).is(".section-up");
  console.log($(this).attr('href'));
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      // Select the section tr
      var section = el.parents("tr.section");
      // Select everything between this section and the other (or tr.total for managing last section)
      var lines = section.nextUntil("tr.section, tr.total");
      
      if (up) {
        var previousSection = section.prevAll("tr.section").first()
        if (previousSection.length > 0) {
          section.insertBefore(previousSection);
          lines.insertAfter(section);
        }
      } else {
        var nextSection = section.nextAll("tr.section, tr.total").first();
        var afterNextSection = nextSection.nextAll("tr.section, tr.total").first();
        if (afterNextSection.length > 0) {
          section.insertBefore(afterNextSection);
          lines.insertAfter(section);
        }
      }
      section.add(lines).effect('highlight');
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});

// Line up'ing / down'ing
$(".line-up, .line-down").live('click', function() {
  var el = $(this);
  var up = $(this).is(".line-up");
  $.ajax({
    url: $(this).attr('href'),
    success: function(data) {
      var line = el.parents("tr.line");
      if (up) {
        // Only if we are not one the first line of a section
        if (line.prev().is(".line")) {
          line.insertBefore(line.prev());
        }
      } else {
      // Only if we are not one the last line of a section
        if (line.next().is(".line")) {
          line.insertAfter(line.next());
        }
      }
      line.effect('highlight');
      refreshHistory();
    },
    error: function() {
      alert("Une erreur est survenue !");
    }
  });
  return false;
});
