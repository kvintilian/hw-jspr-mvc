package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryStubImpl implements PostRepository {

  private final AtomicLong maxPostId = new AtomicLong();
  private final Map<Long, Post> repositoryMap = new ConcurrentHashMap<>();

  public PostRepositoryStubImpl() {
    maxPostId.set(1);
  }

  public List<Post> all() {
    return repositoryMap.values().stream()
            .filter(Predicate.not(Post::isRemoved))
            .collect(Collectors.toList());
  }

  public Optional<Post> getById(long id) {
    Post post = repositoryMap.get(id) != null && !repositoryMap.get(id).isRemoved() ? repositoryMap.get(id) : null;
    return Optional.ofNullable(post);
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      var newId = generateNewId();
      post.setId(newId);
      repositoryMap.put(newId, post);
    } else {
      if (repositoryMap.containsKey(post.getId()) && !repositoryMap.get(post.getId()).isRemoved()) {
        repositoryMap.replace(post.getId(), post);
      } else {
        throw new NotFoundException();
      }
    }
    return post;
  }

  private long generateNewId() {
    return maxPostId.getAndIncrement();
  }

  public void removeById(long id) {
    if (repositoryMap.containsKey(id) && !repositoryMap.get(id).isRemoved()) {
      repositoryMap.get(id).setRemoved(true);
    } else {
      throw new NotFoundException();
    }
  }
}