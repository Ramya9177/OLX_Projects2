package com.zensar.olx.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.zensar.olx.bean.AdvertisementPost;
import com.zensar.olx.bean.AdvertisementStatus;
import com.zensar.olx.bean.Category;
import com.zensar.olx.bean.NewAdvertisementPostRequest;
import com.zensar.olx.bean.NewAdvertisementPostResponse;
import com.zensar.olx.bean.OlxUser;
import com.zensar.olx.service.AdvertisementPostService;

@RestController
public class AdvertisemntpostController {

	@Autowired
	AdvertisementPostService service;

	@PostMapping("/advertise/{un}")
	public NewAdvertisementPostResponse add(@RequestBody NewAdvertisementPostRequest request,
			@PathVariable(name = "un") String userName) {

		AdvertisementPost post = new AdvertisementPost();

		post.setTitle(request.getTitle());
		post.setPrice(request.getPrice());
		post.setDescription(request.getDescription());

		int categoryId = request.getCategoryId();

		RestTemplate restTemplete = new RestTemplate();
		Category category;
		String url = "http://localhost:9052/advertise/getCategoryId/" + categoryId;
		category = restTemplete.getForObject(url, Category.class);

		post.setCategory(category);

		url = "http://localhost:9051/user/find/" + userName;
		OlxUser olxUser = restTemplete.getForObject(url, OlxUser.class);
		post.setOlxUser(olxUser);

		AdvertisementStatus advertisementStatus = new AdvertisementStatus(1, "Open");
		post.setAdvertisementStatus(advertisementStatus);

		AdvertisementPost advertisementPost = this.service.addAdvertisement(post);// entity saved to db

		NewAdvertisementPostResponse response = new NewAdvertisementPostResponse();

		response.setId(advertisementPost.getId());
		response.setTitle(advertisementPost.getTitle());
		response.setPrice(advertisementPost.getPrice());
		response.setCategory(advertisementPost.getCategory().getName());
		response.setDescription(advertisementPost.getDescription());
		response.setUserName(advertisementPost.getOlxUser().getUserName());
		response.setStatus(advertisementPost.getAdvertisementStatus().getStatus());
		response.setCreatedDate(advertisementPost.getCreatedDate());
		response.setModifiedDate(advertisementPost.getModifiedDate());

		return response;
	}

	@PutMapping("/advertise/{aid}/{userName}")
	public NewAdvertisementPostResponse f2(@RequestBody NewAdvertisementPostRequest request,
			@PathVariable(name = "aid") int id, @PathVariable(name = "userName") String userName) {

		AdvertisementPost post = this.service.getAdvertisemntById(id);

		post.setTitle(request.getTitle());
		post.setDescription(request.getDescription());
		post.setPrice(request.getPrice());

		RestTemplate restTemplete = new RestTemplate();

		Category category;
		String url = "http://localhost:9052/advertise/getCategoryId/" + request.getCategoryId();
		category = restTemplete.getForObject(url, Category.class);
		post.setCategory(category);

		url = "http://localhost:9051/user/find/" + userName;
		OlxUser olxUser = restTemplete.getForObject(url, OlxUser.class);
		post.setOlxUser(olxUser);

		url = "http://localhost:9052/advertisement/status/" + request.getStatusId();
		AdvertisementStatus advertisementStatus;
		advertisementStatus = restTemplete.getForObject(url, AdvertisementStatus.class);
		post.setAdvertisementStatus(advertisementStatus);

		AdvertisementPost advertisementPost = this.service.updateAdvertisement(post); // writing in db

		NewAdvertisementPostResponse postResponse;
		postResponse = new NewAdvertisementPostResponse();

		postResponse.setId(advertisementPost.getId());
		postResponse.setTitle(advertisementPost.getTitle());
		postResponse.setDescription(advertisementPost.getDescription());
		postResponse.setPrice(advertisementPost.getPrice());
		postResponse.setUserName(advertisementPost.getOlxUser().getUserName());
		postResponse.setCategory(advertisementPost.getCategory().getName());
		postResponse.setCreatedDate(advertisementPost.getCreatedDate());
		postResponse.setModifiedDate(advertisementPost.getModifiedDate());
		postResponse.setStatus(advertisementPost.getAdvertisementStatus().getStatus());

		return postResponse;

	}

	@GetMapping("/user/advertise/{userName}")
	public List<NewAdvertisementPostResponse> f3(@RequestBody NewAdvertisementPostRequest request,
			@PathVariable(name = "userName") String userName) {
		List<AdvertisementPost> allPosts = this.service.getAllAdvertisements();

		RestTemplate restTemplete = new RestTemplate();

		List<AdvertisementPost> filteredPosts = new ArrayList<>();

		for (AdvertisementPost post : allPosts) {

			String url = "http://localhost:9051/user/find/" + userName;
			OlxUser olxUser = restTemplete.getForObject(url, OlxUser.class);
			// post.setOlxUser(olxUser);

			Category category;
			url = "http://localhost:9052/advertise/getCategoryId/" + post.getCategory().getId();
			category = restTemplete.getForObject(url, Category.class);
			post.setCategory(category);

			url = "http://localhost:9052/advertisement/status/" + post.getAdvertisementStatus().getId();
			AdvertisementStatus advertisementStatus;
			advertisementStatus = restTemplete.getForObject(url, AdvertisementStatus.class);
			post.setAdvertisementStatus(advertisementStatus);

			if (olxUser.getOlxUserId() == post.getOlxUser().getOlxUserId()) {
				post.setOlxUser(olxUser);
				filteredPosts.add(post);

			}
		}
		List<NewAdvertisementPostResponse> responseList = new ArrayList<>();
		for (AdvertisementPost advertisementPost : filteredPosts) {

			NewAdvertisementPostResponse postResponse;
			postResponse = new NewAdvertisementPostResponse();

			postResponse.setId(advertisementPost.getId());
			postResponse.setTitle(advertisementPost.getTitle());
			postResponse.setDescription(advertisementPost.getDescription());
			postResponse.setPrice(advertisementPost.getPrice());
			postResponse.setUserName(advertisementPost.getOlxUser().getUserName());
			postResponse.setCategory(advertisementPost.getCategory().getName());
			postResponse.setCreatedDate(advertisementPost.getCreatedDate());
			postResponse.setModifiedDate(advertisementPost.getModifiedDate());
			postResponse.setStatus(advertisementPost.getAdvertisementStatus().getStatus());
			responseList.add(postResponse);
		}
		System.out.println(responseList);
		return responseList;
	}

	@GetMapping("/user/advertise/{advertiseId}/{userName}")
	public NewAdvertisementPostResponse f4(@RequestBody NewAdvertisementPostRequest request,
			@PathVariable(name = "advertiseId") int id,@PathVariable(name="userName") String userName) {

		AdvertisementPost post = this.service.getAdvertisemntById(id);
		
		RestTemplate restTemplete = new RestTemplate();

		String url = "http://localhost:9051/user/find/" +userName;
		OlxUser olxUser = restTemplete.getForObject(url, OlxUser.class);
		post.setOlxUser(olxUser);
		

		url = "http://localhost:9052/advertise/getCategoryId/" + request.getCategoryId();
		Category category = restTemplete.getForObject(url, Category.class);
		post.setCategory(category);

		url = "http://localhost:9052/advertisement/status/" + request.getStatusId();
		AdvertisementStatus advertisementStatus;
		advertisementStatus = restTemplete.getForObject(url, AdvertisementStatus.class);
		post.setAdvertisementStatus(advertisementStatus);
		

		AdvertisementPost advertisementPost = this.service.getAdvertisemntById(id);

		NewAdvertisementPostResponse postResponse;
		postResponse = new NewAdvertisementPostResponse();

		postResponse.setId(advertisementPost.getId());
		postResponse.setTitle(advertisementPost.getTitle());
		postResponse.setDescription(advertisementPost.getDescription());
		postResponse.setPrice(advertisementPost.getPrice());
		postResponse.setUserName(advertisementPost.getOlxUser().getUserName());
		postResponse.setCategory(advertisementPost.getCategory().getName());
		postResponse.setCreatedDate(advertisementPost.getCreatedDate());
		postResponse.setModifiedDate(advertisementPost.getModifiedDate());
		postResponse.setStatus(advertisementPost.getAdvertisementStatus().getStatus());

		return postResponse;

	}
}
