A = {

	init: function(){
		A.Actions.init();
	}
	,Actions:{
		init: function(){

		}

		,calc: function(e){
			if(this.validate(e)){
				var a = parseFloat(e.a);
				var b = parseFloat(e.b);
				var op = e.op.toString().trim();
				var result = eval(a+op+b);
				return result;
			}
			return null;
		}

		,validate: function(e){

			if(typeof e != 'object'){
				return this.throw(A.Exceptions.EXCEPTION_INC_INPUT_TYPE);
			}

			if((e.a===undefined)+(e.b===undefined)+(e.op===undefined)){

				return this.throw(A.Exceptions.EXCEPTION_NO_FIELD_FOUND);
			}

			var a = parseFloat(e.a);
			var b = parseFloat(e.b);
			var op = e.op.toString().trim();

			if(Number.isNaN(a)||Number.isNaN(b)){
				return this.throw(A.Exceptions.EXCEPTION_UNABLE_TO_CONVERT)
			}

			if((Math.abs(a)==Infinity)||(Math.abs(b)==Infinity)){
				return this.throw(A.Exceptions.EXCEPTION_TOO_LARGE);
			}

			if(!A.Operations.includes(e.op)){
				return this.throw(A.Exceptions.EXCEPTION_UNKNOWN_OPERATION);
				
			}

			return true;
		}

		,throw: function(message){

			console.error(message);
			return false;
		}

	}

	,Operations: ["-","+","/","*","%"]
	,Exceptions:{
		EXCEPTION_INC_INPUT_TYPE: "Incompatible input type: Object expected."
		,EXCEPTION_UNABLE_TO_CONVERT: "Unable to convert entered number to float value"
		,EXCEPTION_NO_FIELD_FOUND: "Unable to proceed: not enough data found for calculations."
		,EXCEPTION_UNKNOWN_OPERATION: "Unknown input operation"
		,EXCEPTION_TOO_LARGE: "Too large numbers entered for calculation."
	}

}

A.init();