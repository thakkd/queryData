module.exports = require('./lib/express');
var express = require('express')
var app = express()

/*
 * body-parser is a piece of express middleware that 
 *   reads a form's input and stores it as a javascript
 *   object accessible through `req.body` 
 *
 * 'body-parser' must be installed (via `npm install --save body-parser`)
 * For more info see: https://github.com/expressjs/body-parser
 */
var bodyParser = require('body-parser');

app.use(bodyParser());

app.get('/', function(req, res){
  // The form's action is '/' and its method is 'POST',
  // so the `app.post('/', ...` route will receive the
  // result of our forma
console.time('handler name');

var MongoClient = require('mongodb').MongoClient
    , format = require('util').format;
MongoClient.connect('mongodb://127.0.0.1:27017/queryData2', function (err, db) {
    if (err) {
        throw err;
    } else {
        console.log("successfully connected to the database");
    }

//Query the w0 collection
// This collection is an inverted data structure.
// Instead of storing the entire file in a database what we really want is a hashtable with keys on the most frequently accessed data.
// So we preprocess the file and populate different collections in mongodb one for each word found in the 0th location
//To our surprise, the data is fairly sanitized and hence could yield a very small data set for first word
// So the inverted structure is basically keeping a track of the top 5 (score wise) query terms 
// for instance in the collection w0 (word 1) we hold the top 5 query texts that being with the word how
// Next when the query arrives, we do a regex match against this collection and display the top hits
// While the output currently only works for the first word, it does demonstrate the basic idea
// We want an in memory hash of the high frequency terms so that we can respond to queries in somewhat constant time
// extension of this could be to have an in memory hash for the length of the input string that we want the service to respond 
//more data analysis can be done on the static data to decide how to shape the solution.


    

    var import1= db.collection('w0');
    var reqq = req.query.q.split(" ");

    var rx = "^"+reqq[0];

    var h = new Object(); //since multiple different records might be returened for the same letter, e.g. w
			 // we want to aggregate the potential query terms and return only the top 5 across

    var a = new Array();
    
  
    //the word field in each record captures the first word of the query phrase
    // for any query term that is less than a word do a regex comparison on w0 collection for
    // the word field  
    //console.log(rx);
    //import1.find({word:req.query.q},{lines:1, _id:0}).toArray(function(err, keywords) {
    import1.find({word: {$regex: rx}},{lines:1, _id:0}).toArray(function(err, keywords) {
        //console.dir(results);
	keywords.forEach(function(item){
		item.lines.forEach(function(ln){
			var tmpArr = Object.keys(ln);

			var re =RegExp('^'+req.query.q,"i");
			//console.log(re);

			//if(ln[tmpArr[0]].match(re) != null){
			if(re.test(ln[tmpArr[0]].match(re))){
				a.push(tmpArr[0]);
				h[tmpArr[0]] = ln[tmpArr[0]];
			}
				
			//console.log(ln[tmpArr[0]]);  //window
				
		})
	});

	// Sort the output array of scores in descending order to select the top 5 linsk
	a.sort(function(a, b) {
		return b - a;
	});

	
	
	var retStringHead = '{ \nq:'+req.query.q+',\nd:[';
	var retStringBody = '';

	for(var xctr = 0; xctr < a.length && xctr < 5; xctr++)
	{
		if(xctr == 0)
			retStringBody = retStringBody + '\"' +  h[a[xctr]] + '\"';
		else	
			retStringBody = retStringBody + ',\"' +  h[a[xctr]] + '\"';	
	}

	retStringBody = retStringBody + ']\n}';

	//res.send(keywords);
	//console.log(retStringHead+retStringBody);
	res.send(retStringHead+retStringBody);

h[a[0]]+'\",\"'+h[a[0]]+'\",\"'+h[a[0]]+'\",\"'+h[a[0]]+'\",\"'
	//console.log(a);
        // Let's close the db
	db.close();
	});

});


console.timeEnd('handler name');
//var html = '<html><body>Results:' + resultsJSON + '</body></html>';
//  var html = '<html> <script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>' +
//	       '<title>Chitika keypress demo' + req.query.q + '</title>' + 
//	       '<form action="/" method="post">' +
  //             'Start typing your text' +
 //              '<input type="text" name="userName" placeholder="..." />' +
 //              '<br>' +
 //              '<button type="submit">Submit</button>' +
 //           '</form></html>';
               
 // res.send(html);
});

// This route receives the posted form.
// As explained above, usage of 'body-parser' means
// that `req.body` will be filled in with the form elements
app.post('/', function(req, res){
  var userName = req.body.userName;
  var html = 'Hello: ' + userName + '.<br>' +
             '<a href="/">Try again.</a>';
  res.send(html);
});

app.listen(80);
