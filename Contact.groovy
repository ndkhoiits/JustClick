/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.social.service.rest;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 5/13/11
 */
@Path("/socialNetwork")
public class SocialNetwork
{
   @GET
   @Path("relationship/{portalContainerName}")
   public Response getConfirmedRelationships(@PathParam("portalContainerName") String portalContainerName, @QueryParam("userName") String userName, @QueryParam("depth") int depth, @QueryParam("displayLimit") int displayLimit) throws Exception
   {
      PortalContainer portalContainer = (PortalContainer)ExoContainerContext.getContainerByName(portalContainerName);

      IdentityManager identityManager = (IdentityManager)portalContainer.getComponentInstanceOfType(IdentityManager.class);
      RelationshipManager relationshipManager = (RelationshipManager)portalContainer.getComponentInstanceOfType(RelationshipManager.class);

      Identity selectedUserIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userName);
      UserNode userNode = new UserNode(userName, userName);
      Profile userProfile = selectedUserIdentity.getProfile();
      if(userProfile != null)
      {
         userNode.setName(userProfile.getFullName());
         userNode.getData().setThumbnail(userProfile.getAvatarImageSource());
      }

      userNode = buildUserNode(userNode, selectedUserIdentity, relationshipManager, depth, displayLimit);

      return Response.ok(userNode, MediaType.APPLICATION_JSON).build();
   }

   /**
    * Build the tree of UserNode depending on input depth
    * @param expandingNode
    * @return
    */
   private UserNode buildUserNode(UserNode expandingNode, Identity identityOfCurrentUser, RelationshipManager relationshipManager, int depth, int displayLimit)  throws Exception
   {
      if(depth == 0)
      {
         return expandingNode;
      }
      else
      {
         List<Relationship> confirmedContacts = relationshipManager.getContacts(identityOfCurrentUser);
         int size = confirmedContacts.size();

         if(displayLimit == 0 || size <= displayLimit)
         {
            for (Relationship contact : confirmedContacts)
            {
               Identity contactIdentity = contact.getSender();
               if (identityOfCurrentUser.getRemoteId().equals(contactIdentity.getRemoteId()))
               {
                  contactIdentity = contact.getReceiver();
               }

               UserNode contactUserNode = new UserNode(contactIdentity);
               contactUserNode = buildUserNode(contactUserNode, contactIdentity, relationshipManager, depth - 1, displayLimit);

               expandingNode.addChild(contactUserNode);

            }
         }
         else
         {
            int startingIndex = new Random().nextInt(size);

            for(int i = 0; i < displayLimit; i++)
            {
               Relationship contact = confirmedContacts.get((startingIndex + i) % size);

               Identity contactIdentity = contact.getSender();
               if (identityOfCurrentUser.getRemoteId().equals(contactIdentity.getRemoteId()))
               {
                  contactIdentity = contact.getReceiver();
               }

               UserNode contactUserNode = new UserNode(contactIdentity);
               contactUserNode = buildUserNode(contactUserNode, contactIdentity, relationshipManager, depth - 1, displayLimit);

               expandingNode.addChild(contactUserNode);
            }
         }

         return expandingNode;
      }
   }

}


/**
 * A class wrapping mandatory data of tree node displayed in hyperbolic tree
 */
class UserNode
{

   private String id;

   private String name;

   private AdditionalData data;

   private List<UserNode> children;

   public UserNode(Identity identity)
   {
      this(identity.getRemoteId(), identity.getRemoteId());
      Profile profile = identity.getProfile();
      if(profile != null)
      {
         this.name = profile.getFullName();
      }
   }

   public UserNode(String id, String name) throws IllegalArgumentException
   {
      if (id == null)
      {
         throw new IllegalArgumentException("Id of instantiating UserNode is null");
      }

      this.id = id;
      this.name = name;
      this.data = new AdditionalData(AdditionalData.DEFAULT_THUMBNAIL);
      this.children = new ArrayList<UserNode>(3);
   }

   public String getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public AdditionalData getData()
   {
      return data;
   }

   public List<UserNode> getChildren()
   {
      return children;
   }

   public void addChild(UserNode child)
   {
      //TODO: Avoid duplicated child
      children.add(child);
   }
}

class AdditionalData
{

   final public static String DEFAULT_THUMBNAIL = "";

   private transient double radial;

   private transient double angular;

   private String thumbnail;

   public AdditionalData(String thumbnail)
   {
      this.thumbnail = thumbnail;
   }

   public String getThumbnail()
   {
      return this.thumbnail;
   }

   public void setThumbnail(String thumbnail)
   {
      this.thumbnail = thumbnail;
   }

   public double getRadial()
   {
      return this.radial;
   }

   public double getAngular()
   {
      return this.angular;
   }

   public void setRadial(double _radial)
   {
      this.radial = _radial;
   }

   public void setAngular(double _angular)
   {
      this.angular = _angular;
   }
}

/**
 * Wrapping radial and angular coordinates of a point in polar axis
 */
class PolarCoordinate
{

   private double radial;

   private double angular;

   public PolarCoordinate(double r, double Phi)
   {
      this.radial = r;
      this.angular = Phi;
   }
}

/**
 * Class providing util methods computing relative polar coordinate
 */
class CoordinateComputator
{

   public static PolarCoordinate computeCoordinate(double parentRadical, int numberOfSibling, int index)
   {
      //The positions of numberOfSibling points make up a quasi-regular polygon
      double radial = (2 * Math.PI * parentRadical) / (2 * numberOfSibling);
      double angular = (2 * Math.PI / numberOfSibling) * (index + 0.5);

      return new PolarCoordinate(radial, angular);
   }
}