package com.example.coffeetica.config;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.CoffeeEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // CoffeeEntity -> CoffeeDetailsDTO mapping
        // ignore fields that are matched manually
        modelMapper.typeMap(CoffeeEntity.class, CoffeeDetailsDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(CoffeeDetailsDTO::setLatestReviews);
                    mapper.skip(CoffeeDetailsDTO::setTotalReviewsCount);
                    mapper.skip(CoffeeDetailsDTO::setAverageRating);
                });

        return modelMapper;
    }
}
