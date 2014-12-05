app.controller('infoController', function(){
	this.section = 0;
	
	this.selectSection = function(setSection) {
      this.section = setSection;
    };

    this.isSet = function(checkSection) {
    	return this.section === checkSection;
    };
  });