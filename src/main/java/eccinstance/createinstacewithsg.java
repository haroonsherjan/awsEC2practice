package eccinstance;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

public class createinstacewithsg {

    public static void main(String[] args) {
        String sgName = "#SecurityGroupName#";
        String sgDesc = "#GroupDescription#";
        String keyName = "#KeyName#";
        String instanceName = "#InstanceName#";
        String amiId = "#AMI-id#"; // Ubuntu 18.04 LTS
        int minInstance = 1;
        int maxInstance = 1;
        createSecurityGroup(sgName, sgDesc);
        createKeyPair(keyName);
        createInstance(instanceName, amiId, sgName, keyName, minInstance, maxInstance);
    }

    public static void createSecurityGroup(String groupName, String desc) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        CreateSecurityGroupRequest createRequest = new CreateSecurityGroupRequest()
                .withGroupName(groupName)
                .withDescription(desc);
        CreateSecurityGroupResult createResponse = ec2.createSecurityGroup(createRequest);
    }

    public static void createKeyPair(String keyName) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        CreateKeyPairRequest request = new CreateKeyPairRequest().withKeyName(keyName);
        CreateKeyPairResult response = ec2.createKeyPair(request);
    }

    public static void createInstance(String name, String amiId, String sgName, String keyName, int min, int max) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        RunInstancesRequest runRequest = new RunInstancesRequest()
                .withImageId(amiId)
                .withInstanceType(InstanceType.T1Micro)
                .withMaxCount(min)
                .withMinCount(max)
                .withKeyName(keyName)
                .withSecurityGroups(sgName);
        RunInstancesResult runResponse = ec2.runInstances(runRequest);
        String reservationId = runResponse.getReservation().getInstances().get(0).getInstanceId();
        Tag tag = new Tag()
                .withKey("Name")
                .withValue(name);
        CreateTagsRequest tagRequest = new CreateTagsRequest()
                .withResources(reservationId)
                .withTags(tag);
        CreateTagsResult tagResponse = ec2.createTags(tagRequest);
        System.out.printf("EC2 instance %s started based on AMI %s\n", reservationId, amiId);
    }

}

