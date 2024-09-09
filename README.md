# Feature Design

## Notifications
The NotificationService implemented provides the function *createNotificationFromUserAndTarget(AppUser user, AppUser
target, ENotificationType  eNotificationType)* for other services to create a notification of their type (FollowService
would create a followed notification whereas FollowRequestService will create a follow request notification).

The standout problem here is that notifications won't be pushed to the users unless they refresh their page, or whenever
the frontend makes a request for the data, not ideal for the user experience. The solution found was to implement
websockets. The current plan is to do an initial fetch of the notifications when a user logs in, then subscribe to a
websocket that will push any new notifications received while the user is on the application.

As JWT authentication is implemented on the server, the websocket is separately managed from the HTTP connections.
The inbound channel is configured to have an interceptor where the JWT authentication is performed. This also sets
the user for the connection, and allows for a unique session id for each user to access their new notifications.

### Types of Notifications

The *target* AppUser is the
one to receive a notification with the *user* AppUser will be the actor of one of the following actions listed in the
ENotificationType enum:
- NOTIFICATION_FOLLOW_REQUEST
  - Received by a user, informing them of a follow request made by another user.
- NOTIFICATION_FOLLOW_ACCEPTED
  - Received by a user, informing them of a user that has accepted their follow request.
- NOTIFICATION_FOLLOW_PUBLIC
  - Received by a user who's privacy setting is on public (no need to accept follow request) that another user has
    followed them.
- NOTIFICATION_UPDATE_LIKED
  - Received by a user that another user has liked their update.
- NOTIFICATION_FOLLOWED_POSTED
  - Received by a user, informing them that a user they follow has posted.

### Batch Processing

The NotificationBatchService runs a function called *sendBatchNotifications()* on a schedule. This function
takes all unsent notifications and bundles them up into a single notification that a user will receive. For example,
instead of seeing 5 notifications of users that liked a certain post, they will see 1 notification with a message like
"5 users have liked your post {insert post preview here}".

The only type of notifications that need batch processing are NOTIFICATION_UPDATE_LIKED and NOTIFICATION_FOLLOWED_POSTED
, the notifications concerned with following users should be sent immediately.

It would be possible for users to click on this batch notification to view the singular notifications within.
This is made possible by the *isSent* and *isRead* boolean values. We can query notifications in the database
that has *isSent* set to true, but *isRead* set to false. However, this would limit users to viewing these detailed
notifications only once. A solution would be to associate grouped notifications to an identifying batch id.
