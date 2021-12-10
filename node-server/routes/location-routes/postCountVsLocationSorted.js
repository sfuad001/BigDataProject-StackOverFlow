var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/location/post-count-location-sorted', function(req, res, next) {
    return res.send({
        "msg": "Hi! Lazin apu's gift"
    })
});

module.exports = router;
