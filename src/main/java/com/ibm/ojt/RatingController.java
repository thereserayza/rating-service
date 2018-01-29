package com.ibm.ojt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DBObject;

@CrossOrigin
@RestController
@RequestMapping("/rate")
public class RatingController {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@GetMapping
	public List<Rating> findAllRating() {
		return mongoTemplate.findAll(Rating.class, "rating");
	}
	
	@PostMapping
	public void saveRating(@RequestBody Rating rating) {
		Query query = new Query().addCriteria(Criteria.where("prodcode").is(rating.getProdcode()).and("customerId").is(rating.getCustomerId()));
		Rating _rating = mongoTemplate.findOne(query, Rating.class, "rating");
		if (_rating == null) {
			mongoTemplate.save(rating, "rating");
		}
	}
	
	@PutMapping
	public void updateRating(@RequestBody Rating rating) {
		Query query = new Query().addCriteria(Criteria.where("prodcode").is(rating.getProdcode()).and("customerId").is(rating.getCustomerId()));
		Rating _rating = mongoTemplate.findOne(query, Rating.class, "rating");
		if (_rating != null) {
			Update update = new Update().set("rate", rating.getRate());
			mongoTemplate.updateFirst(query, update, "rating");
		}
	}
	
	@GetMapping("/avg/{prodcode}")
	public double getAvgRating(@PathVariable String prodcode) {
		Criteria prodCriteria = Criteria.where("prodcode").is(prodcode);
		Aggregation agg = Aggregation.newAggregation(Aggregation.match(prodCriteria), Aggregation.group().avg("rate").as("avgRating"));
		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "rating", DBObject.class);
		double avgRating = Double.valueOf(results.getUniqueMappedResult().get("avgRating").toString());
		return avgRating;
	}
	
	@GetMapping("/count/{prodcode}")
	public int getRateCount(@PathVariable String prodcode) {
		Criteria prodCriteria = Criteria.where("prodcode").is(prodcode);
		Aggregation agg = Aggregation.newAggregation(Aggregation.match(prodCriteria), Aggregation.group().count().as("rateCount"));
		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "rating", DBObject.class);
		int rateCount = Integer.valueOf(results.getUniqueMappedResult().get("rateCount").toString());
		return rateCount;
	}
	
	@GetMapping("/{prodcode}/{customerId}")
	public Rating getUserRating(@PathVariable String prodcode, @PathVariable String customerId) {
		Query query = new Query().addCriteria(Criteria.where("prodcode").is(prodcode).and("customerId").is(customerId));
		return mongoTemplate.findOne(query, Rating.class, "rating");
	}
	
	@GetMapping("/{customerId}")
	public List<Rating> findByCustomerId(@PathVariable String customerId) {
		Query query = new Query().addCriteria(Criteria.where("customerId").is(customerId));
		return mongoTemplate.find(query, Rating.class, "rating");
	}
}
