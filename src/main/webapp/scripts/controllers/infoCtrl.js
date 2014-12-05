app.controller('infoController', function($scope){
	this.section = 0;

	$scope.sections = {
    "sxns": [ 
    	"Offering a Book",
    	 "Requesting a Book", 
    	 "Viewing your Matches", 
    	 "Re-matching",
    	 "Cofirming your Matches",
    	 "Updating your Location and Search Radius",
    	 "Contact Us"
    	 ] 
  };
	
	this.selectSection = function(setSection) {
      this.section = setSection;
    };

    this.isSet = function(checkSection) {
    	return this.section === checkSection;
    };
  });