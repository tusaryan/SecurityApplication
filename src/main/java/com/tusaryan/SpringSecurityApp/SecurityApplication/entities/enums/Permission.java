package com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums;

//W6.5

public enum Permission {

    POST_VIEW,
    POST_CREATE,
    POST_UPDATE,
    POST_DELETE,
    //some more permissions like invalidate a post, attach some post to some other category.
    //any kind of action we want to perform on our post we can create a permission based on that
    //and then we can attach those permissions to some roles/people to perform that action on the resource.

    USER_VIEW,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE

}

