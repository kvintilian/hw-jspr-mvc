package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryStubImpl implements PostRepository {

  private final AtomicLong maxPostId = new AtomicLong();
  private final Map<Long, Post> repositoryMap = new ConcurrentHashMap<>();

  public PostRepositoryStubImpl() {
    maxPostId.set(1);
  }

  public List<Post> all() {
    return new ArrayList<>(repositoryMap.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(repositoryMap.get(id));
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      var newId = generateNewId();
      post.setId(newId);
      repositoryMap.put(newId, post);
    } else {
      if (repositoryMap.containsKey(post.getId())) {
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
    if (repositoryMap.containsKey(id)) {
      repositoryMap.remove(id);
    } else {
      throw new NotFoundException();
    }
  }
}