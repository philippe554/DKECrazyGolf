__kernel void ballSide(__global const float *points,__global const int *sides, __global const float *sidesData,__global float *ball)
{
    int gid = get_global_id(0);
    int lid = get_local_id(0);

    float a = sidesData[gid*9];
    float b = sidesData[gid*9+1];
    float c = sidesData[gid*9+2];
    float d = sidesData[gid*9+3];
    float Nv = sidesData[gid*9+4];
    float3 normal =  {sidesData[gid*9+5],sidesData[gid*9+6],sidesData[gid*9+7]};

    float Nr0=a*ball[lid*11+0]+b*ball[lid*11+1]+c*ball[lid*11+2];
    float t=(d-Nr0)/Nv;
    float3 i = {ball[lid*11+0]+normal.x*t,ball[lid*11+1]+normal.y*t,ball[lid*11+2]+normal.z*t};
    float distance = sqrt(((i.x-ball[lid*11+0])*(i.x-ball[lid*11+0]))+((i.y-ball[lid*11+1])*(i.y-ball[lid*11+1]))+((i.z-ball[lid*11+2])*(i.z-ball[lid*11+2])));

    if(distance<ball[lid*11+9])
    {
        float3 p1 = {points[sides[gid*3]*3],points[sides[gid*3]*3+1],points[sides[gid*3]*3+2]};
        float3 p2 = {points[sides[gid*3+1]*3],points[sides[gid*3+1]*3+1],points[sides[gid*3+1]*3+2]};
        float3 p3 = {points[sides[gid*3+2]*3],points[sides[gid*3+2]*3+1],points[sides[gid*3+2]*3+2]};

        float3 v0={p3.x-p1.x,p3.y-p1.y,p3.z-p1.z};
        float3 v1={p2.x-p1.x,p2.y-p1.y,p2.z-p1.z};
        float3 v2={ i.x-p1.x, i.y-p1.y, i.z-p1.z};

        float v0v0 = v0.x*v0.x + v0.y*v0.y + v0.z*v0.z;
        float v0v1 = v0.x*v1.x + v0.y*v1.y + v0.z*v1.z;
        float v0v2 = v0.x*v2.x + v0.y*v2.y + v0.z*v2.z;
        float v1v1 = v1.x*v1.x + v1.y*v1.y + v1.z*v1.z;
        float v1v2 = v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;

        float u = (v1v1*v0v2-v0v1*v1v2)/(v0v0*v1v1-v0v1*v0v1);
        float v = (v0v0*v1v2-v0v1*v0v2)/(v0v0*v1v1-v0v1*v0v1);
        if(u>=0 && v>=0 && u<=1 && v<=1 && (u+v)<=1)
        {
            float c1 = -((t / fabs(t))*ball[lid*11+9]);
            ball[lid*11+0] = i.x+normal.x*c1;
            ball[lid*11+1] = i.y+normal.y*c1;
            ball[lid*11+2] = i.z+normal.z*c1;
            float c2 = ((ball[lid*11+6]*normal.x)+(ball[lid*11+7]*normal.y)+(ball[lid*11+8]*normal.z))*1.8;
            ball[lid*11+3] = ball[lid*11+6]-normal.x*c2;
            ball[lid*11+4] = ball[lid*11+7]-normal.y*c2;
            ball[lid*11+5] = ball[lid*11+8]-normal.z*c2;
            ball[lid*11+10]=1;
        }
    }
}

__kernel void ballEdge(__global const float *points,__global const int *edges, __global const float *edgesData,__global float *ball)
{
    int gid = get_global_id(0);
    int lid = get_local_id(0);

    float l = edgesData[gid*4];
    float3 unit =  {edgesData[gid*4+1],edgesData[gid*4+2],edgesData[gid*4+3]};

    float3 p1 = {points[edges[gid*2]*3],points[edges[gid*2]*3+1],points[edges[gid*2]*3+2]};
    float3 p2 = {points[edges[gid*2+1]*3],points[edges[gid*2+1]*3+1],points[edges[gid*2+1]*3+2]};

    float t=unit.x*(ball[lid*11+0]-p1.x)+unit.y*(ball[lid*11+1]-p1.y)+unit.z*(ball[lid*11+2]-p1.z);

    if (t > 0 && t < l)
    {
        float3 closest={p1.x+unit.x*t,p1.y+unit.y*t,p1.z+unit.z*t};
        float3 perpendicular = {ball[lid*11+0]-closest.x,ball[lid*11+1]-closest.y,ball[lid*11+2]-closest.z};
        float d = sqrt(perpendicular.x*perpendicular.x+perpendicular.y*perpendicular.y+perpendicular.z*perpendicular.z);
        if(d<ball[lid*11+9])
        {
            float3 perpendicularUnit = {perpendicular.x/d,perpendicular.y/d,perpendicular.z/d};

            ball[lid*11+0] = closest.x+perpendicularUnit.x*ball[lid*11+9];
            ball[lid*11+1] = closest.y+perpendicularUnit.y*ball[lid*11+9];
            ball[lid*11+2] = closest.z+perpendicularUnit.z*ball[lid*11+9];

            float c = ((ball[lid*11+6]*perpendicularUnit.x)+(ball[lid*11+7]*perpendicularUnit.y)+(ball[lid*11+8]*perpendicularUnit.z))*1.8;
            ball[lid*11+3] = ball[lid*11+6]-perpendicularUnit.x*c;
            ball[lid*11+4] = ball[lid*11+7]-perpendicularUnit.y*c;
            ball[lid*11+5] = ball[lid*11+8]-perpendicularUnit.z*c;

            ball[lid*11+10]=1;
        }
    }
}

__kernel void ballPoint(__global const float *points,__global float *ball)
{
    int gid = get_global_id(0);
    int lid = get_local_id(0);

    float3 p1 = {points[gid*3],points[gid*3+1],points[gid*3+2]};

    float3 ballPoint = {ball[lid*11+0]-p1.x,ball[lid*11+1]-p1.y,ball[lid*11+2]-p1.z};

    float d = sqrt(ballPoint.x*ballPoint.x+ballPoint.y*ballPoint.y+ballPoint.z*ballPoint.z);

    if(d<ball[lid*11+9])
    {
        float3 unit = {ballPoint.x/d,ballPoint.y/d,ballPoint.z/d};

        ball[lid*11+0] = ball[lid*11+0]+unit.x*ball[lid*11+9];
        ball[lid*11+1] = ball[lid*11+1]+unit.y*ball[lid*11+9];
        ball[lid*11+2] = ball[lid*11+2]+unit.z*ball[lid*11+9];

        float c = ((ball[lid*11+6]*unit.x)+(ball[lid*11+7]*unit.y)+(ball[lid*11+8]*unit.z))*1.8;
        ball[lid*11+3] = ball[lid*11+6]-unit.x*c;
        ball[lid*11+4] = ball[lid*11+7]-unit.y*c;
        ball[lid*11+5] = ball[lid*11+8]-unit.z*c;

        ball[lid*11+10]=1;
    }
}